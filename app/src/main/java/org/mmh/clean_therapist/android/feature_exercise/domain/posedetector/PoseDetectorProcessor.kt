package org.mmh.clean_therapist.android.feature_exercise.domain.posedetector

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseDetector
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.GraphicOverlay
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.VisionProcessorBase
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/** A processor to run pose detector. */
class PoseDetectorProcessor(
    context: Context,
    options: PoseDetectorOptionsBase,
    private val showInFrameLikelihood: Boolean
) : VisionProcessorBase<PoseDetectorProcessor.PoseWithClassification>(context) {

    private val detector: PoseDetector
    private val classificationExecutor: Executor

    /** Internal class to hold Pose and classification results. */
    class PoseWithClassification(val pose: Pose)

    init {
        detector = PoseDetection.getClient(options)
        classificationExecutor = Executors.newSingleThreadExecutor()
    }

    override fun stop() {
        super.stop()
        detector.close()
    }

    override fun detectInImage(image: InputImage): Task<PoseWithClassification> {
        return detector
            .process(image)
            .continueWith(
                classificationExecutor
            ) { task ->
                val pose = task.result
                PoseWithClassification(pose)
            }
    }

    override fun onSuccess(
        results: PoseWithClassification,
        graphicOverlay: GraphicOverlay
    ) {
        graphicOverlay.add(
            PoseGraphic(
                graphicOverlay,
                results.pose
            )
        )
    }
}
