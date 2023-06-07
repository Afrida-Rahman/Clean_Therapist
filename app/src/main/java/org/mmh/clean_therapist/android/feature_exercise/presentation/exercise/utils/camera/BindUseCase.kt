package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.camera

import android.content.Context
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.PoseDetectorProcessor
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.VisionImageProcessor
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.PreferenceUtils

object BindUseCase {
    fun bindPreviewUseCase(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        cameraProvider: ProcessCameraProvider?,
        previewUseCase: Preview?,
        previewView: PreviewView?,
        cameraSelector: CameraSelector?,
        lensFacing: Int
    ) {
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
        var newPreviewUseCase: Preview? = builder.build()
        newPreviewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
        cameraProvider!!.bindToLifecycle(/* lifecycleOwner = */ lifecycleOwner,
            cameraSelector!!,
            newPreviewUseCase
        )
    }

    fun bindAnalysisUseCase(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        cameraProvider: ProcessCameraProvider?,
        cameraSelector: CameraSelector?,
        lensFacing: Int,
        analysisUseCase: ImageAnalysis?,
        imageProcessor: VisionImageProcessor?
    ) : VisionImageProcessor? {
        if (cameraProvider == null) {
            return imageProcessor
        }
        imageProcessor?.stop()
        val newImageProcessor: VisionImageProcessor? =
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
                return imageProcessor
            }

        val builder = ImageAnalysis.Builder()
        val targetResolution = PreferenceUtils.getCameraXTargetResolution(context, lensFacing)
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution)
        }

        cameraProvider.bindToLifecycle(/* lifecycleOwner = */ lifecycleOwner,
            cameraSelector!!,
            analysisUseCase
        )
        return newImageProcessor
    }
}