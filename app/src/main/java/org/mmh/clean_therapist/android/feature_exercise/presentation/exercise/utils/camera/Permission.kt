package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise.utils.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.content.ContextCompat

object Permission {
    fun isCameraPermissionsGranted(
        context: Context
    ):Boolean{
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    @Composable
    fun GetCameraPermissions(
    launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
    ) {
        SideEffect {
            launcher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }
}