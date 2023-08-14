package com.mtg.tool.findmyphone.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Handler

object MediaPlayerUtil {
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlay = false
    private var handler = Handler()
    private var stopRunnable = Runnable { stopAudio() }
    fun playAudioAssets(context: Context, name: String, duration: Int = 0) {
        restartAudio()
        setDuration(duration)
        val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd(name)
        mediaPlayer.setDataSource(
            assetFileDescriptor.fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        )
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    private fun setDuration(duration: Int) {
        if (duration != 0) {
            handler.postDelayed(stopRunnable, duration.toLong())
            mediaPlayer.setOnCompletionListener { mediaPlayer.start() }
        }
    }

    fun playAudioPath(path: String, duration: Int = 0) {
        restartAudio()
        setDuration(duration)
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
}