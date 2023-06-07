package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.google.mlkit.common.MlKitException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.mmh.clean_therapist.android.core.Resource
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase
import org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.Exercises
import org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.home.GeneralExercise
import org.mmh.clean_therapist.android.feature_exercise.domain.model.exercise.home.HomeExercise
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.GraphicOverlay
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit.VisionImageProcessor
import org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils.PreferenceUtils
import org.mmh.clean_therapist.android.feature_exercise.domain.usecase.networkData.ExerciseUseCases
import org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.camera.BindUseCase.bindAnalysisUseCase
import org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.camera.BindUseCase.bindPreviewUseCase
import org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.camera.Permission.GetCameraPermissions
import org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.camera.Permission.isCameraPermissionsGranted
import javax.inject.Inject

@HiltViewModel
class ExerciseScreenViewModel @Inject constructor(
    private val exerciseUseCases: ExerciseUseCases
) : ViewModel() {

    private lateinit var exercise: Exercise
    lateinit var homeExercise: HomeExercise

    private val _showPauseBtn = MutableLiveData(true)
    val showPauseBtn: LiveData<Boolean> = _showPauseBtn
    private val _showCongrats = MutableLiveData(false)
    val showCongrats: LiveData<Boolean> = _showCongrats

    lateinit var countDisplay: TextView
    lateinit var maxHoldTimeDisplay: TextView
    lateinit var wrongCountDisplay: TextView
    lateinit var distanceDisplay: TextView
    lateinit var phaseDialogueDisplay: TextView
    lateinit var exerciseProgressBar: ProgressBar


    var previewView: PreviewView? = null

    var graphicOverlay: GraphicOverlay? = null
    var cameraProvider: ProcessCameraProvider? = null
    private var previewUseCase: Preview? = null
    private var analysisUseCase: ImageAnalysis? = null
    private var imageProcessor: VisionImageProcessor? = null
    private var needUpdateGraphicOverlayImageSourceInfo = false
    var lensFacing = CameraSelector.LENS_FACING_BACK
    var cameraSelector: CameraSelector? = null

    @Composable
    fun CheckAndGetPermission(
        context: Context,
        launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
    ) {
        if (!isCameraPermissionsGranted(context)) {
            GetCameraPermissions(launcher)
        }
    }

    fun bindAllCameraUseCases(context: Context, lifecycleOwner: LifecycleOwner) {
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
            if (targetResolution != null) {
                builder.setTargetResolution(targetResolution)
            }
            needUpdateGraphicOverlayImageSourceInfo = true
            analysisUseCase = builder.build()
            analysisUseCase?.setAnalyzer(
                ContextCompat.getMainExecutor(context)
            ) { imageProxy: ImageProxy ->
                if ((homeExercise.getSetCount() >= homeExercise.maxSetCount) && !showCongrats.value!!) {
                    _showCongrats.value = !showCongrats.value!!
                }
                if (needUpdateGraphicOverlayImageSourceInfo) {
                    val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT
                    homeExercise.setImageFlipped(isImageFlipped)
                    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                    if (rotationDegrees == 0 || rotationDegrees == 180) {
                        graphicOverlay!!.setImageSourceInfo(
                            imageProxy.width,
                            imageProxy.height,
                            isImageFlipped
                        )
                    } else {
                        graphicOverlay!!.setImageSourceInfo(
                            imageProxy.height,
                            imageProxy.width,
                            isImageFlipped
                        )
                    }
                    needUpdateGraphicOverlayImageSourceInfo = false
                }
                try {
                    graphicOverlay!!.phases = exercise.phases
                    imageProcessor!!.processImageProxy(imageProxy, graphicOverlay).let { person ->
                        if (person != null && homeExercise.rightCountPhases.size != 0 && person.keyPoints.isNotEmpty()) {
                            homeExercise.rightExerciseCount(person, imageProxy.height, imageProxy.width)
                            homeExercise.wrongExerciseCount(person, imageProxy.height, imageProxy.width)
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
                                it.instruction?.let { dialogue ->
                                    if (dialogue.isNotEmpty()) {
                                        if (::phaseDialogueDisplay.isInitialized) {
                                            homeExercise.getPersonDistance(person).let { distance ->
                                                distanceDisplay.text = "%.1f".format(distance)
                                                if (distance <= 5f) {
                                                    phaseDialogueDisplay.textSize = 30f
                                                } else if (5f < distance && distance <= 10f) {
                                                    phaseDialogueDisplay.textSize = 50f
                                                } else {
                                                    phaseDialogueDisplay.textSize = 70f
                                                }
                                            }
                                            phaseDialogueDisplay.visibility = View.VISIBLE
                                            phaseDialogueDisplay.text =
                                                "%s".format(dialogue)
                                        }

                                    } else {
                                        if (::phaseDialogueDisplay.isInitialized)
                                            phaseDialogueDisplay.visibility = View.GONE
                                    }
                                }

                            }
                        }
                    }
                } catch (e: MlKitException) {
                    Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            bindPreviewUseCase(context, lifecycleOwner, cameraProvider, previewUseCase, previewView, cameraSelector, lensFacing)
            imageProcessor = bindAnalysisUseCase(context, lifecycleOwner, cameraProvider, cameraSelector, lensFacing, analysisUseCase, imageProcessor)
        }
    }

    fun setExerciseConstraints(
        context: Context,
        tenant: String,
        exercise: Exercise,
        navController: NavController
    ) {
        this.exercise = exercise
        val existingExercise = Exercises.get(context, exercise.id)
        homeExercise = existingExercise ?: GeneralExercise(
            context = context, exerciseId = exercise.id, active = true
        )
        fetchExerciseConstraints(tenant, context, navController)
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

    private fun fetchExerciseConstraints(
        tenant: String,
        context: Context,
        navController: NavController
    ) {
        viewModelScope.launch {
            exerciseUseCases.fetchExerciseConstraints(
                tenant = tenant,
                exerciseId = exercise.id
            )
                .onEach {
                    when (it) {
                        is Resource.Error -> {
                            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        is Resource.Success -> {
                            it.data?.let { phases ->
                                exercise.phases = phases
                                homeExercise.setConsideredIndices(
                                    phases
                                )
                                homeExercise.rightCountPhases =
                                    phases.sortedBy { it -> it.id } as MutableList<Phase>
                                homeExercise.rightCountPhases =
                                    homeExercise.sortedPhaseList(
                                        homeExercise.rightCountPhases.toList()
                                    )
                                        .toMutableList()

                            }
                        }
                    }
                }.launchIn(this)
        }
    }

    fun onResume(context: Context, lifecycleOwner: LifecycleOwner) {
        bindAllCameraUseCases(context, lifecycleOwner)
    }

    fun onPause() {
        imageProcessor?.run { this.stop() }
    }

    fun onDestroy() {
        imageProcessor?.run { this.stop() }
    }
}