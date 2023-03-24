package org.mmh.clean_therapist.android.feature_exercise.domain.posedetector

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import org.mmh.clean_therapist.android.core.model.Point
import com.google.common.primitives.Ints
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import org.mmh.clean_therapist.android.core.util.Draw
import org.mmh.clean_therapist.android.core.util.Utilities
import org.mmh.clean_therapist.android.feature_exercise.domain.model.BodyPart
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.GraphicOverlay
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.VisualUtils
import java.util.*

/** Draw the detected pose in preview.  */
class PoseGraphic(
    overlay: GraphicOverlay,
    private val pose: Pose,
    private val showInFrameLikelihood: Boolean,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean,
    private val poseClassification: List<String>
) : GraphicOverlay.Graphic(overlay) {
    private var zMin = java.lang.Float.MAX_VALUE
    private var zMax = java.lang.Float.MIN_VALUE
    private val classificationTextPaint: Paint = Paint()
    private val leftPaint: Paint
    private val rightPaint: Paint
    private val whitePaint: Paint

    val MIN_CONFIDENCE = 0.3f
    val LINE_WIDTH = 3f
    val BORDER_WIDTH = 10f

    private val MAPPINGS = listOf(
        listOf(BodyPart.LEFT_EAR.position, BodyPart.LEFT_EYE.position),
        listOf(BodyPart.LEFT_EYE.position, BodyPart.NOSE.position),
        listOf(BodyPart.NOSE.position, BodyPart.RIGHT_EYE.position),
        listOf(BodyPart.RIGHT_EYE.position, BodyPart.RIGHT_EAR.position),
        listOf(BodyPart.LEFT_SHOULDER.position, BodyPart.MID_SHOULDER.position),
        listOf(BodyPart.MID_SHOULDER.position, BodyPart.RIGHT_SHOULDER.position),
        listOf(BodyPart.LEFT_SHOULDER.position, BodyPart.LEFT_ELBOW.position),
        listOf(BodyPart.LEFT_ELBOW.position, BodyPart.LEFT_WRIST.position),
        listOf(BodyPart.LEFT_SHOULDER.position, BodyPart.LEFT_HIP.position),
        listOf(BodyPart.LEFT_HIP.position, BodyPart.LEFT_KNEE.position),
        listOf(BodyPart.LEFT_KNEE.position, BodyPart.LEFT_ANKLE.position),
        listOf(BodyPart.LEFT_HIP.position, BodyPart.MID_HIP.position),
        listOf(BodyPart.MID_HIP.position, BodyPart.RIGHT_HIP.position),
        listOf(BodyPart.RIGHT_SHOULDER.position, BodyPart.RIGHT_ELBOW.position),
        listOf(BodyPart.RIGHT_ELBOW.position, BodyPart.RIGHT_WRIST.position),
        listOf(BodyPart.RIGHT_SHOULDER.position, BodyPart.RIGHT_HIP.position),
        listOf(BodyPart.RIGHT_HIP.position, BodyPart.RIGHT_KNEE.position),
        listOf(BodyPart.RIGHT_KNEE.position, BodyPart.RIGHT_ANKLE.position)
    )

    init {
        classificationTextPaint.color = Color.WHITE
        classificationTextPaint.textSize = POSE_CLASSIFICATION_TEXT_SIZE
        classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK)

        whitePaint = Paint()
        whitePaint.strokeWidth = STROKE_WIDTH
        whitePaint.color = Color.WHITE
        whitePaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE
        leftPaint = Paint()
        leftPaint.strokeWidth = STROKE_WIDTH
        leftPaint.color = Color.GREEN
        rightPaint = Paint()
        rightPaint.strokeWidth = STROKE_WIDTH
        rightPaint.color = Color.YELLOW
    }

    override fun drawBodyKeyPoints(canvas: Canvas) {
        val landmarks = pose.allPoseLandmarks
        if (landmarks.isEmpty()) {
            return
        }
        val draw = Draw(canvas, Color.WHITE, LINE_WIDTH)
        val width = draw.canvas.width
        val height = draw.canvas.height

        val person = VisualUtils.landmarkToPerson(pose)
        val isFrontCamera = false

        MAPPINGS.forEach { map ->
            val startPoint = person.keyPoints[map[0]].toCanvasPoint()
            val endPoint = person.keyPoints[map[1]].toCanvasPoint()
            if (person.keyPoints[map[0]].score >= MIN_CONFIDENCE && person.keyPoints[map[1]].score >= MIN_CONFIDENCE) {
                if (isFrontCamera) {
                    draw.line(
                        Point(
                            width - startPoint.x,
                            startPoint.y
                        ),
                        Point(
                            width - endPoint.x,
                            endPoint.y
                        ),
                        _color = Color.rgb(170, 255, 0)
                    )
                } else {
                    draw.line(
                        Point(translateX(startPoint.x),
                            translateY(startPoint.y)),
                        Point(translateX(endPoint.x),
                            translateY(endPoint.y)), _color = Color.rgb(170, 255, 0))
                }
            }
        }
    }

    private fun drawPoint(canvas: Canvas, landmark: PoseLandmark, paint: Paint) {
        val point = landmark.position3D
        maybeUpdatePaintColor(paint, canvas, point.z)
        canvas.drawCircle(translateX(point.x), translateY(point.y), DOT_RADIUS, paint)
    }

    private fun drawLine(
        canvas: Canvas,
        startLandmark: PoseLandmark?,
        endLandmark: PoseLandmark?,
        paint: Paint
    ) {
        val start = startLandmark!!.position3D
        val end = endLandmark!!.position3D

        // Gets average z for the current body line
        val avgZInImagePixel = (start.z + end.z) / 2
        maybeUpdatePaintColor(paint, canvas, avgZInImagePixel)

        canvas.drawLine(
            translateX(start.x),
            translateY(start.y),
            translateX(end.x),
            translateY(end.y),
            paint
        )
    }

    private fun maybeUpdatePaintColor(
        paint: Paint,
        canvas: Canvas,
        zInImagePixel: Float
    ) {
        if (!visualizeZ) {
            return
        }

        // When visualizeZ is true, sets up the paint to different colors based on z values.
        // Gets the range of z value.
        val zLowerBoundInScreenPixel: Float
        val zUpperBoundInScreenPixel: Float

        if (rescaleZForVisualization) {
            zLowerBoundInScreenPixel = kotlin.math.min(-0.001f, scale(zMin))
            zUpperBoundInScreenPixel = kotlin.math.max(0.001f, scale(zMax))
        } else {
            // By default, assume the range of z value in screen pixel is [-canvasWidth, canvasWidth].
            val defaultRangeFactor = 1f
            zLowerBoundInScreenPixel = -defaultRangeFactor * canvas.width
            zUpperBoundInScreenPixel = defaultRangeFactor * canvas.width
        }

        val zInScreenPixel = scale(zInImagePixel)

        if (zInScreenPixel < 0) {
            // Sets up the paint to draw the body line in red if it is in front of the z origin.
            // Maps values within [zLowerBoundInScreenPixel, 0) to [255, 0) and use it to control the
            // color. The larger the value is, the more red it will be.
            var v = (zInScreenPixel / zLowerBoundInScreenPixel * 255).toInt()
            v = Ints.constrainToRange(v, 0, 255)
            paint.setARGB(255, 255, 255 - v, 255 - v)
        } else {
            // Sets up the paint to draw the body line in blue if it is behind the z origin.
            // Maps values within [0, zUpperBoundInScreenPixel] to [0, 255] and use it to control the
            // color. The larger the value is, the more blue it will be.
            var v = (zInScreenPixel / zUpperBoundInScreenPixel * 255).toInt()
            v = Ints.constrainToRange(v, 0, 255)
            paint.setARGB(255, 255 - v, 255 - v, 255)
        }
    }

    companion object {
        private const val DOT_RADIUS = 8.0f
        private const val IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f
        private const val STROKE_WIDTH = 10.0f
        private const val POSE_CLASSIFICATION_TEXT_SIZE = 60.0f
    }
}
