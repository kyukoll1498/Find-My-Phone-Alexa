package com.mtg.tool.findmyphone.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler

object MediaPlayerUtil {
    lateinit var mediaPlayer: MediaPlayer
    var isPlay = false
    var handler = Handler()
    var stopRunnable = Runnable { stopAudio() }
    fun playAudioAssets(context: Context, name: String, duration: Int = 0, callback: () -> Unit) {
        restartAudio()
        setDuration(duration, callback)
        val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd(name)
        mediaPlayer.setDataSource(
            assetFileDescriptor.fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        )
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    private fun setDuration(duration: Int, callback: () -> Unit) {
        if (duration != 0) {
            handler.postDelayed(stopRunnable, duration.toLong())
            mediaPlayer.setOnCompletionListener { mediaPlayer.start() }
        } else {
            mediaPlayer.setOnCompletionListener {
                callback.invoke()
                stopAudio()
            }
        }
    }

    fun playAudioPath(path: String, duration: Int = 0, callback: () -> Unit) {
        restartAudio()
        setDuration(duration, callback)
        mediaPlayer.setDataSource(path)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    private fun restartAudio() {
        if (isPlay) {
            stopAudio()
        }
        isPlay = true
        mediaPlayer = MediaPlayer()
    }
    fun stopAudio() {
        handler.removeCallbacks(stopRunnable)
        isPlay = false
        try {
            mediaPlayer.stop()
            mediaPlayer.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pauseAudio() {
        try {
            mediaPlayer.pause()
        } catch (e: Exception) {
          e.printStackTrace()
        }

    }

    fun getDurationFromFile(path: String?): Int {
        if (path == null) {
            return 0
        }
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(path)
        return mediaPlayer.duration
    }


}