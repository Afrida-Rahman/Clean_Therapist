package org.mmh.clean_therapist.android.feature_exercise.domain.repository

import org.mmh.clean_therapist.android.feature_exercise.data.dto.AssessmentDto
import org.mmh.clean_therapist.android.feature_exercise.data.dto.ExerciseListDto
import org.mmh.clean_therapist.android.feature_exercise.data.dto.PhaseDto
import org.mmh.clean_therapist.android.feature_exercise.domain.payload.AssessmentPayload
import org.mmh.clean_therapist.android.feature_exercise.domain.payload.ExerciseConstraintPayload
import org.mmh.clean_therapist.android.feature_exercise.domain.payload.ExerciseListPayload
import retrofit2.http.Body
import retrofit2.http.POST

interface RemoteAssessmentRepository {

    @POST("/api/exercisekeypoint/GetPatientAssessments")
    suspend fun fetchAssessments(@Body payload: AssessmentPayload): AssessmentDto

    @POST("/api/exercisekeypoint/GetPatientExercises")
    suspend fun fetchExercises(@Body payload: ExerciseListPayload): ExerciseListDto

    @POST("/api/exercisekeypoint/GetPatientExerciseRestrictions")
    suspend fun fetchExerciseConstraints(@Body payload: ExerciseConstraintPayload): PhaseDto
}