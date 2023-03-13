package org.mmh.clean_therapist.android.feature_exercise.domain.usecase

import org.mmh.clean_therapist.android.core.Resource
import org.mmh.clean_therapist.android.feature_exercise.data.dto.toExerciseList
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Exercise
import org.mmh.clean_therapist.android.feature_exercise.domain.payload.ExerciseListPayload
import org.mmh.clean_therapist.android.feature_exercise.domain.repository.RemoteAssessmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class FetchExercises @Inject constructor(
    private val repository: RemoteAssessmentRepository
) {
    operator fun invoke(
        tenant: String,
        testId: String
    ): Flow<Resource<List<Exercise>>> = flow {
        emit(Resource.Loading())
        try {
            val exerciseListDto = repository.fetchExercises(
                payload = ExerciseListPayload(
                    tenant = tenant,
                    testId = testId
                )
            )
            emit(
                Resource.Success(
                    exerciseListDto.toExerciseList()
                )
            )
        } catch (e: IOException) {
            emit(Resource.Error("Could not reach to the server"))
        } catch (e: HttpException) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred!"))
        }
    }
}