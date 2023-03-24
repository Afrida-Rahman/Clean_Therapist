package org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils

import android.graphics.PointF
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
        val midShoulderY = (leftShoulder!!.position.y + rightShoulder!!.position.y)/2
        val midShoulderScore = (leftShoulder!!.inFrameLikelihood + rightShoulder!!.inFrameLikelihood)/2

        val midHipX = (leftHip!!.position.x + rightHip!!.position.x)/2
        val midHipY = (leftHip!!.position.y + rightHip!!.position.y)/2
        val midHipScore = (leftHip!!.inFrameLikelihood + rightHip!!.inFrameLikelihood)/2

        var keyPoints: List<KeyPoint> = listOf(
            KeyPoint(BodyPart.NOSE, nose!!.position, nose.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_EYE, leftEye!!.position, leftEye.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_EYE, rightEye!!.position, rightEye.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_EAR, leftEar!!.position, leftEar.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_EAR, rightEar!!.position, rightEar.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_SHOULDER, leftShoulder!!.position, leftShoulder.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_SHOULDER, rightShoulder!!.position, rightShoulder.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_ELBOW, leftElbow!!.position, leftElbow.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_ELBOW, rightElbow!!.position, rightElbow.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_WRIST, leftWrist!!.position, leftWrist.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_WRIST, rightWrist!!.position, rightWrist.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_HIP, leftHip!!.position, leftHip.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_HIP, rightHip!!.position, rightHip.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_KNEE, leftKnee!!.position, leftKnee.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_KNEE, rightKnee!!.position, rightKnee.inFrameLikelihood),
            KeyPoint(BodyPart.LEFT_ANKLE, leftAnkle!!.position, leftAnkle.inFrameLikelihood),
            KeyPoint(BodyPart.RIGHT_ANKLE, rightAnkle!!.position, rightAnkle.inFrameLikelihood),
            KeyPoint(BodyPart.MID_SHOULDER, PointF(midShoulderX, midShoulderY), midShoulderScore),
            KeyPoint(BodyPart.MID_HIP, PointF(midHipX, midHipY), midHipScore)
        )

        var maxScore = 0f;
        for (keypoint in keyPoints) {
            if (keypoint.score > maxScore)
                maxScore = keypoint.score
        }

        return Person(keyPoints, maxScore)
    }
}