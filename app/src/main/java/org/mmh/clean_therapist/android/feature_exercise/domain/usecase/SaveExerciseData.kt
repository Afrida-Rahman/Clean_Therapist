package org.mmh.clean_therapist.android.feature_exercise.domain.usecase

import org.mmh.clean_therapist.android.core.Resource
import org.mmh.clean_therapist.android.core.util.Utilities
import org.mmh.clean_therapist.android.feature_exercise.data.dto.ExerciseTrackingDto
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise
import org.mmh.clean_therapist.android.feature_exercise.domain.payload.ExerciseTrackingPayload
import org.mmh.clean_therapist.android.feature_exercise.domain.repository.RemoteExerciseTrackingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SaveExerciseData @Inject constructor(
    private val repository: RemoteExerciseTrackingRepository
) {
    operator fun invoke(
        exercise: Exercise,
        testId: String,
        patientId: String,
        noOfReps: Int,
        noOfSets: Int,
        noOfWrongCount: Int,
        tenant: String
    ): Flow<Resource<ExerciseTrackingDto>> = flow {
        emit(Resource.Loading())
        try {
            val payload = ExerciseTrackingPayload(
                exerciseId = exercise.id,
                testId = testId,
                protocolId = exercise.protocolId,
                patientId = patientId,
                exerciseDate = Utilities.currentDate(),
                noOfReps = noOfReps,
                noOfSets = noOfSets,
                noOfWrongCount = noOfWrongCount,
                tenant = tenant
            )
            val exerciseTrackingDto = repository.saveExerciseData(payload = payload)
            if (exerciseTrackingDto.success) {
                emit(Resource.Success(exerciseTrackingDto))
            } else {
                emit(Resource.Error(exerciseTrackingDto.message))
            }
        } catch (e: IOException) {
            emit(Resource.Error("Could not reach to the server"))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred!"))
        }
    }
}