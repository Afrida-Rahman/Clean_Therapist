package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.exercise

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.common.MlKitException
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase
import org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.Exercises
import org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.home.GeneralExercise
import org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.home.HomeExercise
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.GraphicOverlay
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.VisionImageProcessor
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.PreferenceUtils

class ExerciseAnalyzer {
    lateinit var exercise: Exercise
    lateinit var homeExercise: HomeExercise
    private var needUpdateGraphicOverlayImageSourceInfo = false
    private val _showCongrats = MutableLiveData(false)
    val showCongrats: LiveData<Boolean> = _showCongrats

    var cameraProvider: ProcessCameraProvider? = null
    var previewUseCase: Preview? = null
    var analysisUseCase: ImageAnalysis? = null
    var imageProcessor: VisionImageProcessor? = null
    var lensFacing = CameraSelector.LENS_FACING_BACK
    var cameraSelector: CameraSelector? = null

    lateinit var countDisplay: TextView
    lateinit var maxHoldTimeDisplay: TextView
    lateinit var wrongCountDisplay: TextView
    lateinit var distanceDisplay: TextView
    lateinit var phaseDialogueDisplay: TextView
    lateinit var timeCountDisplay: TextView
    lateinit var exerciseProgressBar: ProgressBar


    var previewView: PreviewView? = null

    var graphicOverlay: GraphicOverlay? = null

    fun buildExerciseAnalyzer(
        context: Context
    ) {
        if (cameraProvider != null) {
            cameraProvider!!.unbindAll()
            if (analysisUseCase != null) {
                cameraProvider!!.unbind(analysisUseCase)
            }
            val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                for (cameraId in manager.cameraIdList) {
                    val characteristics = manager.getCameraCharacteristics(cameraId)
                    homeExercise.setFocalLength(characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS))
                }
            } catch (_: CameraAccessException) {
            }
            val builder = ImageAnalysis.Builder()
            val targetResolution = PreferenceUtils.getCameraXTargetResolution(context, lensFacing)
            if (targetResolution != null) builder.setTargetResolution(targetResolution)

            needUpdateGraphicOverlayImageSourceInfo = true
            analysisUseCase = builder.build()
            analysisUseCase!!.setAnalyzer(
                ContextCompat.getMainExecutor(context)
            ) { imageProxy: ImageProxy ->
                if ((homeExercise.getSetCount() >= homeExercise.maxSetCount) && !showCongrats.value!!) {
                    _showCongrats.value = !showCongrats.value!!
                }
                if (needUpdateGraphicOverlayImageSourceInfo) {
                    val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                    homeExercise.setImageFlipped(isImageFlipped)
                    graphicOverlay!!.setImageSourceInfo(
                        imageProxy.width,
                        imageProxy.height,
                        isImageFlipped
                    )
                    needUpdateGraphicOverlayImageSourceInfo = false
                }
                try {
                    graphicOverlay!!.phases = exercise.phases
                    imageProcessor!!.processImageProxy(imageProxy, graphicOverlay).let { person ->
                        if (person != null && homeExercise.rightCountPhases.size != 0 && person.keyPoints.isNotEmpty()) {
                            homeExercise.rightExerciseCount(
                                person, imageProxy.height, imageProxy.width
                            )
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
                                val timeToDisplay = homeExercise.getHoldTimeLimitCount()
                                it.instruction?.let { dialogue ->
                                    if (dialogue.isNotEmpty()) {
                                        homeExercise.getPersonDistance(person).let { distance ->
                                            distanceDisplay.text = "%.1f".format(distance)
                                            phaseDialogueDisplay.textSize = when {
                                                distance <= 5f -> 30f
                                                distance > 5f && distance <= 10f -> 50f
                                                else -> 70f
                                            }
                                        }
                                        phaseDialogueDisplay.visibility = View.VISIBLE
                                        phaseDialogueDisplay.text =
                                            "%s".format(dialogue)
                                    } else {
                                        phaseDialogueDisplay.visibility = View.GONE
                                    }
                                }
                                if (timeToDisplay > 0) {
                                    timeCountDisplay.visibility = View.VISIBLE
                                    timeCountDisplay.text =
                                        "%d".format(timeToDisplay)
                                } else {
                                    timeCountDisplay.visibility = View.GONE
                                    timeCountDisplay.text =
                                        "%d".format(0)
                                }
                            }
                        }
                    }
                } catch (e: MlKitException) {
                    Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    fun initializeExercise(
        context: Context,
        exercise: Exercise
    ) {
        this.exercise = exercise
        val existingExercise = Exercises.get(context, exercise.id)
        homeExercise = existingExercise ?: GeneralExercise(
            context = context, exerciseId = exercise.id
        )
        homeExercise.setExercise(
            exerciseName = exercise.name,
            exerciseInstruction = "",
            exerciseImageUrls = listOf(),
            exerciseVideoUrls = "",
            repetitionLimit = exercise.repetition,
            setLimit = exercise.set,
            protoId = exercise.protocolId,
        )
    }

    fun updateExerciseConstraints(phases: List<Phase>) {
        exercise.phases = phases
        homeExercise.setConsideredIndices(phases)
        homeExercise.rightCountPhases = phases.sortedBy { it.id } as MutableList<Phase>
        homeExercise.rightCountPhases =
            homeExercise.sortedPhaseList(homeExercise.rightCountPhases.toList()).toMutableList()
    }
}