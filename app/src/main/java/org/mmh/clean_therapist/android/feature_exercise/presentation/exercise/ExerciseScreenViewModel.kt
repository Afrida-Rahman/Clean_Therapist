package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.common.MlKitException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.mmh.clean_therapist.android.core.Resource
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.PoseDetectorProcessor
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.GraphicOverlay
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.VisionImageProcessor
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.PreferenceUtils
import org.mmh.clean_therapist.android.feature_exercise.domain.usecase.ExerciseUseCases
import javax.inject.Inject

@HiltViewModel
class ExerciseScreenViewModel @Inject constructor(
    private val exerciseUseCases: ExerciseUseCases) : ViewModel() {
    private lateinit var exercise: Exercise

    @SuppressLint("StaticFieldLeak")
    var previewView: PreviewView? = null
    @SuppressLint("StaticFieldLeak")
    var graphicOverlay: GraphicOverlay? = null
    var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    var lensFacing = CameraSelector.LENS_FACING_BACK
    var cameraSelector: CameraSelector? = null

    @Composable
    fun CheckAndGetPermission(
        context: Context,
        launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
    ) {
        if (!allRuntimePermissionsGranted(context)) {
            GetRuntimePermissions(context, launcher)
        }
    }

    private fun allRuntimePermissionsGranted(context: Context): Boolean {
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission.let {
                if (!isPermissionGranted(context, it)) {
                    return false
                }
            }
        }
        return true
    }

    @Composable
    private fun GetRuntimePermissions(
        context: Context,
        launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
    ) {
        val permissionsToRequest = java.util.ArrayList<String>()
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission.let {
                if (!isPermissionGranted(context, it)) {
                    permissionsToRequest.add(permission)
                }
            }
        }
        println("Required permissions: $REQUIRED_RUNTIME_PERMISSIONS")
        println("Permissions to request: $permissionsToRequest")
        for (permission in permissionsToRequest) {
            SideEffect {
                println("Requesting: $permission")
                launcher.launch(permissionsToRequest.toTypedArray())
            }
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

    fun bindAllCameraUseCases(context: Context, lifecycleOwner: LifecycleOwner) {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider!!.unbindAll()
            bindPreviewUseCase(context, lifecycleOwner)
            bindAnalysisUseCase(context, lifecycleOwner)
        }
    }

    private fun bindPreviewUseCase(context: Context, lifecycleOwner: LifecycleOwner) {
        if (cameraProvider == null) {
            return
        }
        if (previewUseCase != null) {
            cameraProvider!!.unbind(previewUseCase)
        }

        val builder = Preview.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(context, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner = */ lifecycleOwner,
            cameraSelector!!,
            previewUseCase
        )
    }

    private fun bindAnalysisUseCase(context: Context, lifecycleOwner: LifecycleOwner) {
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
                val poseDetectorOptions =
                    PreferenceUtils.getPoseDetectorOptionsForLivePreview(context)
                PoseDetectorProcessor(
                    context = context,
                    poseDetectorOptions,
                    showInFrameLikelihood = true
                )
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Can not create image processor: " + e.localizedMessage,
                    Toast.LENGTH_LONG
                )
                    .show()
                return
            }

        val builder = ImageAnalysis.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(context, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        analysisUseCase = builder.build()

        needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase?.setAnalyzer(
            ContextCompat.getMainExecutor(context)
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
                graphicOverlay!!.phases = exercise.phases
                imageProcessor!!.processImageProxy(imageProxy, graphicOverlay)
            } catch (e: MlKitException) {
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner = */ lifecycleOwner,
            cameraSelector!!,
            analysisUseCase
        )
    }

    fun setExerciseConstraints(tenant: String, exercise: Exercise) {
        this.exercise = exercise
        fetchExerciseConstraints(tenant)
    }

    private fun fetchExerciseConstraints(tenant: String) {
        viewModelScope.launch {
            exerciseUseCases.fetchExerciseConstraints(tenant = tenant, exerciseId = exercise.id)
                .onEach {
                    when (it) {
                        is Resource.Error -> {

                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            it.data?.let { phases ->
                                exercise.phases = phases
                            }
                        }
                    }
                }.launchIn(this)
        }
    }

    fun onResume(context: Context, lifecycleOwner: LifecycleOwner) {
        bindAllCameraUseCases(context, lifecycleOwner)
    }

    fun onPause() {
        imageProcessor?.run { this.stop() }
    }

    fun onDestroy() {
        imageProcessor?.run { this.stop() }
    }

    companion object {
        private val REQUIRED_RUNTIME_PERMISSIONS =
            arrayOf(
                Manifest.permission.CAMERA
            )
    }

}