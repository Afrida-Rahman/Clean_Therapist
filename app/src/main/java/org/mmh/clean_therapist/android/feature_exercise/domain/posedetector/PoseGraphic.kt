package org.mmh.clean_therapist.android.feature_exercise.domain.posedetector

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import com.google.mlkit.vision.pose.Pose
import org.mmh.clean_therapist.android.core.util.Draw
import org.mmh.clean_therapist.android.feature_exercise.domain.model.BodyPart
import org.mmh.clean_therapist.android.feature_exercise.domain.model.KeyPoint
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Person
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.GraphicOverlay
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.VisualUtils

/** Draw the detected pose in preview.  */
class PoseGraphic(
    overlay: GraphicOverlay,
    private val pose: Pose,
) : GraphicOverlay.Graphic(overlay) {
    private val classificationTextPaint: Paint = Paint()
    private val leftPaint: Paint
    private val rightPaint: Paint
    private val whitePaint: Paint

    private val minConfidence = 0.3f
    private val lineWidth = 3f

    private val mappings = listOf(
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

    override fun drawBodyKeyPoints(canvas: Canvas, phases: List<Phase>) : Person? {
        val landmarks = pose.allPoseLandmarks
        if (landmarks.isEmpty()) {
            return null
        }
        val draw = Draw(canvas, Color.WHITE, lineWidth)
        draw.canvas.width

        val keyPoints: MutableList<KeyPoint> = mutableListOf()
        for (keyPoint in VisualUtils.landmarkToPerson(pose).keyPoints) {
            val coordinate = PointF(translateX(keyPoint.coordinate.x), translateY(keyPoint.coordinate.y))
            keyPoints += KeyPoint(keyPoint.bodyPart, coordinate, keyPoint.score)
        }
        val person = Person(keyPoints, VisualUtils.landmarkToPerson(pose).score)

        mappings.forEach { map ->
            val startPoint = person.keyPoints[map[0]].toCanvasPoint()
            val endPoint = person.keyPoints[map[1]].toCanvasPoint()
            if (person.keyPoints[map[0]].score >= minConfidence && person.keyPoints[map[1]].score >= minConfidence) {
                draw.line(
                    startPoint,
                    endPoint, _color = Color.rgb(170, 255, 0))
            }
        }

        for (phase in phases) {
            for (constraint in phase.constraints) {
                constraint.draw(draw, person)
            }
        }
        return person
    }

    companion object {
        private const val IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f
        private const val STROKE_WIDTH = 10.0f
        private const val POSE_CLASSIFICATION_TEXT_SIZE = 60.0f
    }
}
