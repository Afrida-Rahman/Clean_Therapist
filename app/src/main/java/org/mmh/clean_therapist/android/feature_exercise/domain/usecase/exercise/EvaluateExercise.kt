package org.mmh.clean_therapist.android.feature_exercise.domain.usecase.exercise

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.common.MlKitException
import org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.home.HomeExercise
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.PoseDetectorProcessor
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.GraphicOverlay
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.VisionImageProcessor
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.PreferenceUtils

class EvaluateExercise {
    lateinit var homeExercise: HomeExercise
    operator fun invoke(context: Context,
                        lifecycleOwner: LifecycleOwner,
                        previewView: PreviewView,
                        cameraProvider: ProcessCameraProvider,
                        graphicOverlay: GraphicOverlay,
                        imageProcessor: VisionImageProcessor,
                        cameraSelector: CameraSelector,
                        lensFacing: Int

    ){

    }
    private fun bindPreviewUseCase(context: Context,
                                   lifecycleOwner: LifecycleOwner,
                                   previewView: PreviewView,
                                   cameraProvider: ProcessCameraProvider,
                                   cameraSelector: CameraSelector,
                                   lensFacing: Int
    ) {
        val builder = Preview.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(context, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }
        var previewUseCase: Preview? = null
        previewUseCase = builder.build()
        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner = */ lifecycleOwner,
            cameraSelector!!,
            previewUseCase
        )
    }
    private fun bindAnalysisUseCase(context: Context,
                                    lifecycleOwner: LifecycleOwner,
                                    imageProcessor: VisionImageProcessor?,,
                                    lensFacing: Int
    ) {
        imageProcessor!!.stop()

        val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (cameraId in manager.cameraIdList) {
                val characteristics = manager.getCameraCharacteristics(cameraId)
                homeExercise.setFocalLength(characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS))
            }
        } catch (_: CameraAccessException) {

        }
        var newImageProcessor: VisionImageProcessor? =
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
        val analysisUseCase: ImageAnalysis = builder.build()

        var needUpdateGraphicOverlayImageSourceInfo = true

        analysisUseCase.setAnalyzer(
            ContextCompat.getMainExecutor(context)
        ) { imageProxy: ImageProxy ->
            if ((homeExercise.getSetCount() >= homeExercise.maxSetCount) && !showCongrats.value!!) {
                _showCongrats.value = !showCongrats.value!!
            }
            if (needUpdateGraphicOverlayImageSourceInfo) {
                val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                homeExercise.setImageFlipped(isImageFlipped)
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
                newImageProcessor!!.processImageProxy(imageProxy, graphicOverlay).let { person ->
                    if (person != null && homeExercise.rightCountPhases.size != 0 && person.keyPoints.isNotEmpty()) {
                        homeExercise.rightExerciseCount(person, imageProxy.height, imageProxy.width)
                        homeExercise.wrongExerciseCount(person, imageProxy.height, imageProxy.width)
                        countDisplay.text = "%d/%d".format(
                            homeExercise.getRepetitionCount(), homeExercise.getSetCount()
                        )
                        maxHoldTimeDisplay.text =
                            "%d".format(homeExercise.getMaxHoldTime())
                        exerciseProgressBar.progress =
                            homeExercise.getSetCount() * homeExercise.maxRepCount + homeExercise.getRepetitionCount()
                        wrongCountDisplay.text =
                            "%d".format(homeExercise.getWrongCount())
                        homeExercise.getPhase()?.let {
                            it.instruction?.let { dialogue ->
                                if (dialogue.isNotEmpty()) {
                                    if (::phaseDialogueDisplay.isInitialized) {
                                        homeExercise.getPersonDistance(person).let { distance ->
                                            distanceDisplay.text = "%.1f".format(distance)
                                            if (distance <= 5f) {
                                                phaseDialogueDisplay.textSize = 30f
                                            } else if (5f < distance && distance <= 10f) {
                                                phaseDialogueDisplay.textSize = 50f
                                            } else {
                                                phaseDialogueDisplay.textSize = 70f
                                            }
                                        }
                                        phaseDialogueDisplay.visibility = View.VISIBLE
                                        phaseDialogueDisplay.text =
                                            "%s".format(dialogue)
                                    }

                                } else {
                                    if (::phaseDialogueDisplay.isInitialized)
                                        phaseDialogueDisplay.visibility = View.GONE
                                }
                            }

                        }
                    }
                }
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
}