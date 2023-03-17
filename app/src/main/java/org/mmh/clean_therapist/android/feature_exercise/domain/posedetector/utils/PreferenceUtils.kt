package org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.utils

import android.content.Context
import android.preference.PreferenceManager
import android.util.Size
import androidx.camera.core.CameraSelector
import com.google.common.base.Preconditions
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions

object PreferenceUtils {
    fun getCameraXTargetResolution(context: Context, lensFacing: Int): Size? {
        Preconditions.checkArgument(
            lensFacing == CameraSelector.LENS_FACING_BACK
                    || lensFacing == CameraSelector.LENS_FACING_FRONT
        )
        val prefKey =
            if (lensFacing == CameraSelector.LENS_FACING_BACK) "crctas" else " cfctas"
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return try {
            Size.parseSize(sharedPreferences.getString(prefKey, null))
        } catch (e: Exception) {
            null
        }
    }

    fun getPoseDetectorOptionsForLivePreview(context: Context): PoseDetectorOptionsBase {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val preferGPU = sharedPreferences.getBoolean("pdpg", true)
        val builder = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
        if (preferGPU) {
            builder.setPreferredHardwareConfigs(AccuratePoseDetectorOptions.CPU_GPU)
        }
        return builder.build()
    }
}