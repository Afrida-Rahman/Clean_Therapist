package org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils

import android.content.ContentValues.TAG
import android.graphics.PointF
import android.util.Log
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import org.mmh.clean_therapist.android.feature_exercise.domain.model.BodyPart
import org.mmh.clean_therapist.android.feature_exercise.domain.model.KeyPoint
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Person

object VisualUtils {
    fun landmarkToPerson(
        pose: Pose
    ): Person {
        val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
        val leftEye = pose.getPoseLandmark(PoseLandmark.LEFT_EYE)
        val rightEye = pose.getPoseLandmark(PoseLandmark.RIGHT_EYE)
        val leftEar = pose.getPoseLandmark(PoseLandmark.LEFT_EAR)
        val rightEar = pose.getPoseLandmark(PoseLandmark.RIGHT_EAR)

        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)

        val midShoulderX = (leftShoulder!!.position.x + rightShoulder!!.position.x)/2
        val midShoulderY = (leftShoulder.position.y + rightShoulder.position.y)/2
        val midShoulderScore = (leftShoulder.inFrameLikelihood + rightShoulder.inFrameLikelihood)/2

        val midHipX = (leftHip!!.position.x + rightHip!!.position.x)/2
        val midHipY = (leftHip.position.y + rightHip.position.y)/2
        val midHipScore = (leftHip.inFrameLikelihood + rightHip.inFrameLikelihood)/2

        val keyPoints: List<KeyPoint> = listOf(
            KeyPoint(BodyPart.NOSE, nose!!.position, nose.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_EYE, leftEye!!.position, leftEye.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_EYE, rightEye!!.position, rightEye.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_EAR, leftEar!!.position, leftEar.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_EAR, rightEar!!.position, rightEar.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_SHOULDER, leftShoulder.position, leftShoulder.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_SHOULDER, rightShoulder.position, rightShoulder.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_ELBOW, leftElbow!!.position, leftElbow.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_ELBOW, rightElbow!!.position, rightElbow.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_WRIST, leftWrist!!.position, leftWrist.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_WRIST, rightWrist!!.position, rightWrist.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_HIP, leftHip.position, leftHip.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_HIP, rightHip.position, rightHip.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_KNEE, leftKnee!!.position, leftKnee.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_KNEE, rightKnee!!.position, rightKnee.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_ANKLE, leftAnkle!!.position, leftAnkle.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_ANKLE, rightAnkle!!.position, rightAnkle.inFrameLikelihood),
            KeyPoint(BodyPart.MID_SHOULDER, PointF(midShoulderX, midShoulderY), midShoulderScore),
            KeyPoint(BodyPart.MID_HIP, PointF(midHipX, midHipY), midHipScore)
        )

        var maxScore = 0f
        for (keypoint in keyPoints) {
            if (keypoint.score > maxScore)
                maxScore = keypoint.score
        }

        return Person(keyPoints, maxScore)
    }

    fun getIndex(name: String): Int {
        return when (name) {
            "NOSE".lowercase() -> BodyPart.NOSE.position
            "LEFT_EYE".lowercase() -> BodyPart.LEFT_EYE.position
            "RIGHT_EYE".lowercase() -> BodyPart.RIGHT_EYE.position
            "LEFT_EAR".lowercase() -> BodyPart.LEFT_EAR.position
            "RIGHT_EAR".lowercase() -> BodyPart.RIGHT_EAR.position
            "LEFT_SHOULDER".lowercase() -> BodyPart.LEFT_SHOULDER.position
            "RIGHT_SHOULDER".lowercase() -> BodyPart.RIGHT_SHOULDER.position
            "LEFT_ELBOW".lowercase() -> BodyPart.LEFT_ELBOW.position
            "RIGHT_ELBOW".lowercase() -> BodyPart.RIGHT_ELBOW.position
            "LEFT_WRIST".lowercase() -> BodyPart.LEFT_WRIST.position
            "RIGHT_WRIST".lowercase() -> BodyPart.RIGHT_WRIST.position
            "LEFT_HIP".lowercase() -> BodyPart.LEFT_HIP.position
            "RIGHT_HIP".lowercase() -> BodyPart.RIGHT_HIP.position
            "LEFT_KNEE".lowercase() -> BodyPart.LEFT_KNEE.position
            "RIGHT_KNEE".lowercase() -> BodyPart.RIGHT_KNEE.position
            "LEFT_ANKLE".lowercase() -> BodyPart.LEFT_ANKLE.position
            "RIGHT_ANKLE".lowercase() -> BodyPart.RIGHT_ANKLE.position
            "MID_SHOULDER".lowercase() -> BodyPart.MID_SHOULDER.position
            "MID_HIP".lowercase() -> BodyPart.MID_HIP.position
            else -> -1
        }
    }

    fun getIndexName(name: Int): String {
        return when (name) {
            BodyPart.NOSE.position -> "NOSE".lowercase()
            BodyPart.LEFT_EYE.position -> "LEFT_EYE".lowercase()
            BodyPart.RIGHT_EYE.position -> "RIGHT_EYE".lowercase()
            BodyPart.LEFT_EAR.position -> "LEFT_EAR".lowercase()
            BodyPart.RIGHT_EAR.position -> "RIGHT_EAR".lowercase()
            BodyPart.LEFT_SHOULDER.position -> "LEFT_SHOULDER".lowercase()
            BodyPart.RIGHT_SHOULDER.position -> "RIGHT_SHOULDER".lowercase()
            BodyPart.LEFT_ELBOW.position -> "LEFT_ELBOW".lowercase()
            BodyPart.RIGHT_ELBOW.position -> "RIGHT_ELBOW".lowercase()
            BodyPart.LEFT_WRIST.position -> "LEFT_WRIST".lowercase()
            BodyPart.RIGHT_WRIST.position -> "RIGHT_WRIST".lowercase()
            BodyPart.LEFT_HIP.position -> "LEFT_HIP".lowercase()
            BodyPart.RIGHT_HIP.position -> "RIGHT_HIP".lowercase()
            BodyPart.LEFT_KNEE.position -> "LEFT_KNEE".lowercase()
            BodyPart.RIGHT_KNEE.position -> "RIGHT_KNEE".lowercase()
            BodyPart.LEFT_ANKLE.position -> "LEFT_ANKLE".lowercase()
            BodyPart.RIGHT_ANKLE.position -> "RIGHT_ANKLE".lowercase()
            BodyPart.MID_SHOULDER.position -> "MID_SHOULDER".lowercase()
            BodyPart.MID_HIP.position -> "MID_HIP".lowercase()
            else -> ""
        }
    }

    fun isInsideBox(
        person: Person,
        consideredIndices: List<Int>,
        canvasHeight: Int,
        canvasWidth: Int
    ): Boolean {
        var rightPosition = true
        person.keyPoints.forEach {
            if (it.bodyPart.position in consideredIndices) {
                val x = it.coordinate.x
                val y = it.coordinate.y
//                if (x < 0 || x > canvasWidth || y < 0 || y > canvasHeight) {
//                    Log.d(TAG, "isInsideBox: ${it.bodyPart.position} -> $x, $y")
//                    rightPosition = false
//                }
                if (it.score < 0.3f) {
                    rightPosition = false
                }
            }
        }
        return rightPosition
    }

}