package org.mmh.clean_therapist.android.feature_authentication.presentation.sign_in

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.mmh.clean_therapist.android.core.Resource
import org.mmh.clean_therapist.android.core.UIEvent
import org.mmh.clean_therapist.android.core.util.Utilities
import org.mmh.clean_therapist.android.feature_authentication.domain.usecase.PatientUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val patientUseCases: PatientUseCases,
    private val preferences: SharedPreferences
) : ViewModel() {
    private val _email = mutableStateOf("")
    val email: State<String> = _email

    private val _password = mutableStateOf("")
    val password: State<String> = _password

    private val _showPassword = mutableStateOf(false)
    val showPassword: State<Boolean> = _showPassword

    private val _tenant = mutableStateOf("")
    val tenant: State<String> = _tenant

    private val _showCircularProgress = mutableStateOf(false)
    val showCircularProgressIndicator: State<Boolean> = _showCircularProgress

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun onEvent(event: SignInEvent) {
        when (event) {
            is SignInEvent.EnteredEmail -> {
                _email.value = event.email
            }
            is SignInEvent.EnteredPassword -> {
                _password.value = event.password
            }
            is SignInEvent.EnteredTenant -> {
                _tenant.value = event.tenant
            }
            is SignInEvent.ShowPassword -> {
                _showPassword.value = !showPassword.value
            }
            is SignInEvent.ShowCircularProgress -> {
                _showCircularProgress.value = !showCircularProgressIndicator.value
            }
            is SignInEvent.SignInButtonClick -> {
                viewModelScope.launch {
                    if (!showCircularProgressIndicator.value) {
                        patientUseCases.patientInformation(
                            email = email.value,
                            password = password.value,
                            tenant = tenant.value
                        ).onEach {
                            when (it) {
                                is Resource.Loading<*> -> {
                                    _showCircularProgress.value = true
                                }
                                is Resource.Success<*> -> {
                                    _showCircularProgress.value = false
                                    _email.value = ""
                                    _password.value = ""
                                    _eventFlow.emit(UIEvent.ShowSnackBar(message = "Successfully signed in"))
                                    it.data?.let { patient ->
                                        Utilities.savePatient(
                                            preferences = preferences,
                                            data = patient
                                        )
                                    }
                                    event.onSuccess()
                                }
                                is Resource.Error<*> -> {
                                    _showCircularProgress.value = false
                                    _password.value = ""
                                    _eventFlow.emit(
                                        UIEvent.ShowSnackBar(
                                            message = it.message ?: "Unknown error"
                                        )
                                    )
                                }
                            }
                        }.launchIn(this)
                    }
                }
            }
        }
    }
}