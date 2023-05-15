package org.mmh.clean_therapist.android.core.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.annotation.RawRes

class AudioPlayer(
    private val context: Context
) {
    fun playFromFile(@RawRes filepath: Int) {
        val player = MediaPlayer.create(context, filepath)
        player.start()
        player.setOnCompletionListener {
            player.release()
        }
    }

    fun playFromUrl(url: String) {
        val player = MediaPlayer()
        val uri = Uri.parse(url)
        val attributes = AudioAttributes
            .Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        player.setAudioAttributes(attributes)
        player.setDataSource(context, uri)
        player.prepare()
        player.start()
        player.setOnCompletionListener {
            player.release()
        }
    }
}