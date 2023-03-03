package org.mmh.clean_therapist.android.feature_authentication.presentation.splash

import android.content.SharedPreferences
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.mmh.clean_therapist.android.core.util.Utilities
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    preferences: SharedPreferences
) : ViewModel() {
    private val _isAlreadyLoggedIn = mutableStateOf(false)
    val isAlreadyLoggedIn: State<Boolean> = _isAlreadyLoggedIn

    private val _isWalkThroughShown = mutableStateOf(false)
    val isWalkThroughShown: State<Boolean> = _isWalkThroughShown

    init {
        val data = Utilities.getPatient(preferences = preferences)
        _isAlreadyLoggedIn.value = data.loggedIn
        _isWalkThroughShown.value = data.walkThroughPageShown
    }
}