package org.mmh.clean_therapist.android.feature_exercise.domain.repository

import org.mmh.clean_therapist.android.feature_exercise.data.dto.ExerciseTrackingDto
import org.mmh.clean_therapist.android.feature_exercise.domain.payload.ExerciseTrackingPayload
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RemoteExerciseTrackingRepository {

    @Headers("Authorization: Bearer YXBpdXNlcjpZV2xoYVlUUmNHbDFjMlZ5T2lRa1RVWVRFUk1ESXc=")
    @POST("/api/exercise/SaveExerciseTracking")
    suspend fun saveExerciseData(@Body payload: ExerciseTrackingPayload): ExerciseTrackingDto
}