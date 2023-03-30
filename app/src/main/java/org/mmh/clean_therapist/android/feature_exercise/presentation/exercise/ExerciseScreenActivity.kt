package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.platform.LocalView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.google.mlkit.common.MlKitException
import org.mmh.clean_therapist.R
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.PoseDetectorProcessor
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.GraphicOverlay
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.VisionImageProcessor
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.PreferenceUtils
import org.mmh.clean_therapist.android.feature_exercise.presentation.CommonEvent
import org.mmh.clean_therapist.android.feature_exercise.presentation.CommonViewModel

class ExerciseScreenActivity :
    AppCompatActivity(), View.OnClickListener {

    private var previewView: PreviewView? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private var selectedModel = POSE_DETECTION
    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private var cameraSelector: CameraSelector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }

        val exerciseId = intent.getIntExtra("exerciseId", 0)
        val tenant = intent.getStringExtra("tenant")
        val testId = intent.getStringExtra("testId")

        if (tenant != null && exerciseId != null && testId != null) {
            ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            )[CommonViewModel::class.java]
                .fetchExerciseConstraints(tenant, testId, exerciseId)
        }

        if (!allRuntimePermissionsGranted()) {
            getRuntimePermissions()
        }
        if (savedInstanceState != null) {
            selectedModel = savedInstanceState.getString(STATE_SELECTED_MODEL, POSE_DETECTION)
        }
        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        setContentView(R.layout.activity_exercise_screen)
        previewView = findViewById(R.id.preview_view)
        graphicOverlay = findViewById(R.id.graphic_overlay)
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }
        val facingSwitch = findViewById<ImageButton>(R.id.camera_switch_button)
        facingSwitch.setOnClickListener(this)
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[ExerciseViewModel::class.java]
            .processCameraProvider
            .observe(
                this
            ) { provider: ProcessCameraProvider? ->
                cameraProvider = provider
                bindAllCameraUseCases()
            }
    }

    override fun onClick(v: View?) {
        if (cameraProvider == null) {
            return
        }
        val newLensFacing =
            if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                CameraSelector.LENS_FACING_BACK
            } else {
                CameraSelector.LENS_FACING_FRONT
            }
        val newCameraSelector = CameraSelector.Builder().requireLensFacing(newLensFacing).build()
        try {
            if (cameraProvider!!.hasCamera(newCameraSelector)) {
                Log.d(TAG, "Set facing to $newLensFacing")
                lensFacing = newLensFacing
                cameraSelector = newCameraSelector
                bindAllCameraUseCases()
                return
            }
        } catch (e: CameraInfoUnavailableException) {
            // Falls through
        }
        Toast.makeText(
            applicationContext,
            "This device does not have lens with facing: $newLensFacing",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    private fun bindAllCameraUseCases() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider!!.unbindAll()
            bindPreviewUseCase()
            bindAnalysisUseCase()
        }
    }

    private fun bindPreviewUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        val builder = Preview.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner = */ this,
            cameraSelector!!,
            previewUseCase
        )
    }

    private fun bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return
        }
        if (analysisUseCase != null) {
            cameraProvider!!.unbind(analysisUseCase)
        }
        if (imageProcessor != null) {
            imageProcessor!!.stop()
        }
        imageProcessor =
            try {
                when (selectedModel) {
                    POSE_DETECTION -> {
                        val poseDetectorOptions =
                            PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
                        PoseDetectorProcessor(
                            context = this,
                            poseDetectorOptions,
                            showInFrameLikelihood = true
                        )
                    }
                    else -> throw IllegalStateException("Invalid model name")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Can not create image processor: $selectedModel", e)
                Toast.makeText(
                    applicationContext,
                    "Can not create image processor: " + e.localizedMessage,
                    Toast.LENGTH_LONG
                )
                    .show()
                return
            }

        val builder = ImageAnalysis.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        analysisUseCase = builder.build()

        needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase?.setAnalyzer(
            // imageProcessor.processImageProxy will use another thread to run the detection underneath,
            // thus we can just runs the analyzer itself on main thread.
            ContextCompat.getMainExecutor(this)
        ) { imageProxy: ImageProxy ->
            if (needUpdateGraphicOverlayImageSourceInfo) {
                val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                if (rotationDegrees == 0 || rotationDegrees == 180) {
                    graphicOverlay!!.setImageSourceInfo(
                        imageProxy.width,
                        imageProxy.height,
                        isImageFlipped
                    )
                } else {
                    graphicOverlay!!.setImageSourceInfo(
                        imageProxy.height,
                        imageProxy.width,
                        isImageFlipped
                    )
                }
                needUpdateGraphicOverlayImageSourceInfo = false
            }
            try {
                imageProcessor!!.processImageProxy(imageProxy, graphicOverlay)
            } catch (e: MlKitException) {
                Log.e(TAG, "Failed to process image. Error: " + e.localizedMessage)
                Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner = */ this,
            cameraSelector!!,
            analysisUseCase
        )
    }

    private fun allRuntimePermissionsGranted(): Boolean {
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission.let {
                if (!isPermissionGranted(this, it)) {
                    return false
                }
            }
        }
        return true
    }

    private fun getRuntimePermissions() {
        val permissionsToRequest = java.util.ArrayList<String>()
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission.let {
                if (!isPermissionGranted(this, it)) {
                    permissionsToRequest.add(permission)
                }
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUESTS
            )
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("CameraPermission", "Permission granted: $permission")
            return true
        }
        Log.i("CameraPermission", "Permission NOT granted: $permission")
        return false
    }

    companion object {
        private const val PERMISSION_REQUESTS = 1

        private val REQUIRED_RUNTIME_PERMISSIONS =
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        private const val TAG = "CameraXLivePreview"
        private const val POSE_DETECTION = "Pose Detection"
        private const val STATE_SELECTED_MODEL = "selected_model"
    }
}