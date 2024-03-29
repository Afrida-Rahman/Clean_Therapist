package org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit

import androidx.camera.core.ImageProxy
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Person

/**
 * An interface to process the images with different vision detectors and custom image models.
 */
interface VisionImageProcessor {
    /**
     * Processes ImageProxy image data, e.g. used for CameraX live preview case.
     */
    fun processImageProxy(
        image: ImageProxy?,
        graphicOverlay: GraphicOverlay?
    ) : Person?

    /**
     * Stops the underlying machine learning model and release resources.
     */
    fun stop()
}