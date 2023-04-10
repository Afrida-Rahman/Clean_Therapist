package org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.annotation.GuardedBy
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.common.InputImage
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.BitmapUtils
import java.nio.ByteBuffer

/**
 * Abstract base class for ML Kit frame processors. Subclasses need to implement {@link
 * #onSuccess(T, FrameMetadata, GraphicOverlay)} to define what they want to with the detection
 * results and {@link #detectInImage(VisionImage)} to specify the detector object.
 *
 * @param <T> The type of the detected feature.
 */
abstract class VisionProcessorBase<T>(context: Context) : VisionImageProcessor {

    private var activityManager: ActivityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private val executor = ScopedExecutor(TaskExecutors.MAIN_THREAD)

    // Whether this processor is already shut down
    private var isShutdown = false

    // To keep the latest images and its metadata.
    @GuardedBy("this")
    private var latestImage: ByteBuffer? = null

    @GuardedBy("this")
    private var latestImageMetaData: FrameMetadata? = null

    // To keep the images and metadata in process.
    @GuardedBy("this")
    private var processingImage: ByteBuffer? = null

    @GuardedBy("this")
    private var processingMetaData: FrameMetadata? = null

    @Synchronized
    private fun processLatestImage(graphicOverlay: GraphicOverlay) {
        processingImage = latestImage
        processingMetaData = latestImageMetaData
        latestImage = null
        latestImageMetaData = null
        if (processingImage != null && processingMetaData != null && !isShutdown) {
            processImage(processingImage!!, processingMetaData!!, graphicOverlay)
        }
    }

    private fun processImage(
        data: ByteBuffer,
        frameMetadata: FrameMetadata,
        graphicOverlay: GraphicOverlay
    ) {
        // If live viewport is on (that is the underneath surface view takes care of the camera preview
        // drawing), skip the unnecessary bitmap creation that used for the manual preview drawing.
        val bitmap = BitmapUtils.getBitmap(data, frameMetadata)

        requestDetectInImage(
            InputImage.fromByteBuffer(
                data,
                frameMetadata.width,
                frameMetadata.height,
                frameMetadata.rotation,
                InputImage.IMAGE_FORMAT_NV21
            ),
            graphicOverlay,
            bitmap
        )
            .addOnSuccessListener(executor) { processLatestImage(graphicOverlay) }
    }

    // -----------------Code for processing live preview frame from CameraX API-----------------------
    @ExperimentalGetImage
    override fun processImageProxy(
        image: ImageProxy?,
        graphicOverlay: GraphicOverlay?
    ) {
        if (isShutdown) {
            return
        }
        val bitmap: Bitmap? = image?.let { BitmapUtils.getBitmap(it) }

        if (graphicOverlay != null) {
            requestDetectInImage(
                InputImage.fromMediaImage(image?.image!!, image.imageInfo.rotationDegrees),
                graphicOverlay,
                /* originalCameraImage= */ bitmap
            )
                // When the image is from CameraX analysis use case, must call image.close() on received
                // images when finished using them. Otherwise, new images may not be received or the camera
                // may stall.
                .addOnCompleteListener { image.close() }
        }
    }

    // -----------------Common processing logic-------------------------------------------------------
    private fun requestDetectInImage(
        image: InputImage,
        graphicOverlay: GraphicOverlay,
        originalCameraImage: Bitmap?
    ): Task<T> {
        return setUpListener(
            detectInImage(image),
            graphicOverlay,
            originalCameraImage
        )
    }

    private fun setUpListener(
        task: Task<T>,
        graphicOverlay: GraphicOverlay,
        originalCameraImage: Bitmap?,
    ): Task<T> {
        return task
            .addOnSuccessListener(
                executor
            ) { results: T ->
                graphicOverlay.clear()
                if (originalCameraImage != null) {
                    graphicOverlay.add(CameraImageGraphic(graphicOverlay, originalCameraImage))
                }
                this@VisionProcessorBase.onSuccess(results, graphicOverlay)
                graphicOverlay.postInvalidate()
            }
            .addOnFailureListener(
                executor
            ) { e: Exception ->
                graphicOverlay.clear()
                graphicOverlay.postInvalidate()
                Toast.makeText(
                    graphicOverlay.context,
                    """Failed to process. Error: " + ${e.localizedMessage}Cause: ${e.cause}""".trimIndent(),
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun stop() {
        executor.shutdown()
        isShutdown = true
    }

    protected abstract fun detectInImage(image: InputImage): Task<T>
    protected abstract fun onSuccess(results: T, graphicOverlay: GraphicOverlay)

}
