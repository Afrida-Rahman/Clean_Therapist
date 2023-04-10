package org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit

import android.graphics.Bitmap
import android.graphics.Canvas
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase

/** Draw camera image to background.  */
class CameraImageGraphic(overlay: GraphicOverlay, private val bitmap: Bitmap) :
    GraphicOverlay.Graphic(overlay) {

    override fun drawBodyKeyPoints(canvas: Canvas, phases: List<Phase>) {
        canvas.drawBitmap(bitmap, getTransformationMatrix(), null)
    }
}