package org.mmh.clean_therapist.android.feature_authentication.data.data_source

import androidx.room.*
import org.mmh.clean_therapist.android.feature_authentication.domain.model.Patient
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {

    @Query("SELECT * FROM patient")
    fun getPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patient WHERE loggedIn = 1")
    suspend fun getLoggedInPatient(): Patient?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient)

    @Delete
    suspend fun deletePatient(patient: Patient)
}