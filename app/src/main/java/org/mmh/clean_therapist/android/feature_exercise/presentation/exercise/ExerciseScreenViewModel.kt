package org.mmh.clean_therapist.android.feature_exercise.presentation.exercise

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.mmh.clean_therapist.android.feature_exercise.domain.usecase.ExerciseUseCases
import javax.inject.Inject

@HiltViewModel
class ExerciseScreenViewModel @Inject constructor(
    private val exerciseUseCases: ExerciseUseCases,
    preferences: SharedPreferences
) : ViewModel() {
    @Composable
    fun checkAndGetPermission(
        context: Context,
        launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
    ) {
        if (!allRuntimePermissionsGranted(context)) {
            getRuntimePermissions(context, launcher)
        }
    }
    private fun allRuntimePermissionsGranted(context: Context): Boolean {
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission.let {
                if (!isPermissionGranted(context, it)) {
                    return false
                }
            }
        }
        return true
    }
    @Composable
    private fun getRuntimePermissions(
        context: Context,
        launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>
    ) {
        val permissionsToRequest = java.util.ArrayList<String>()
        for (permission in REQUIRED_RUNTIME_PERMISSIONS) {
            permission.let {
                if (!isPermissionGranted(context, it)) {
                    permissionsToRequest.add(permission)
                }
            }
        }
        println("Required permissions: $REQUIRED_RUNTIME_PERMISSIONS")
        println("Permissions to request: $permissionsToRequest")
        for (permission in permissionsToRequest) {
            SideEffect {
                println("Requesting: $permission")
                launcher.launch(permissionsToRequest.toTypedArray())
            }
        }
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("CameraPermission", "Permission granted: $permission")
            return true
        }
        Log.i("CameraPermission", "Permission NOT granted: $permission")
        return false
    }

    companion object {
        private const val PERMISSION_REQUESTS = 1

        private val REQUIRED_RUNTIME_PERMISSIONS =
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        private const val TAG = "CameraXLivePreview"
        private const val POSE_DETECTION = "Pose Detection"
        private const val STATE_SELECTED_MODEL = "selected_model"
    }

}