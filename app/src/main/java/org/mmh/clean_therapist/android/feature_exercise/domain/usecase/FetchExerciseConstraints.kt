package org.mmh.clean_therapist.android.feature_exercise.domain.usecase

import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mmh.clean_therapist.android.core.Resource
import org.mmh.clean_therapist.android.feature_exercise.data.dto.toPhaseList
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase
import org.mmh.clean_therapist.android.feature_exercise.domain.payload.ExerciseConstraintPayload
import org.mmh.clean_therapist.android.feature_exercise.domain.repository.RemoteAssessmentRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class FetchExerciseConstraints @Inject constructor(
    private val repository: RemoteAssessmentRepository
) {
    operator fun invoke(
        tenant: String,
        exerciseId: Int
    ): Flow<Resource<List<Phase>>> = flow {
        emit(Resource.Loading())
        try {
            Log.d(TAG, "invoke: $exerciseId")
            val exercisePhaseDto = repository.fetchExerciseConstraints(
                payload = ExerciseConstraintPayload(
                    tenant = tenant,
                    exerciseId = exerciseId
                )
            )
            Log.d(TAG, "invoke: ${exercisePhaseDto.toPhaseList()}")
            emit(
                Resource.Success(
                    exercisePhaseDto.toPhaseList()
                )
            )
        } catch (e: IOException) {
            emit(Resource.Error("Could not reach to the server"))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred!"))
        }
    }
}