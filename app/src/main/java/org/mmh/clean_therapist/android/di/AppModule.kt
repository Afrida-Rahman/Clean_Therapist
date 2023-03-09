package org.mmh.clean_therapist.android.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.mmh.clean_therapist.android.core.util.Urls
import org.mmh.clean_therapist.android.feature_authentication.data.data_source.PatientDatabase
import org.mmh.clean_therapist.android.feature_authentication.data.repository.LocalPatientRepositoryImpl
import org.mmh.clean_therapist.android.feature_authentication.domain.repository.LocalPatientRepository
import org.mmh.clean_therapist.android.feature_authentication.domain.repository.RemotePatientRepository
import org.mmh.clean_therapist.android.feature_authentication.domain.usecase.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesPatientDatabase(application: Application): PatientDatabase {
        return Room.databaseBuilder(
            application,
            PatientDatabase::class.java,
            PatientDatabase.DB_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun providesSharedPreference(application: Application): SharedPreferences {
        return application.getSharedPreferences("patientData", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providesPatientRepositoryLocal(db: PatientDatabase): LocalPatientRepository {
        return LocalPatientRepositoryImpl(db.patientDao)
    }

    @Provides
    @Singleton
    fun providesRemotePatientRepository(): RemotePatientRepository {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Urls.get("emma"))
            .build()
            .create(RemotePatientRepository::class.java)
    }

    @Provides
    @Singleton
    fun providesPatientUseCases(
        repositoryPatientRepository: LocalPatientRepository,
        remote: RemotePatientRepository
    ): PatientUseCases {
        return PatientUseCases(
            getLoggedInPatient = GetLoggedInPatient(repositoryPatientRepository),
            getPatients = GetPatients(repositoryPatientRepository),
            insertPatient = InsertPatient(repositoryPatientRepository),
            deletePatient = DeletePatient(repositoryPatientRepository),
            patientInformation = PatientInformation(remote)
        )
    }
}