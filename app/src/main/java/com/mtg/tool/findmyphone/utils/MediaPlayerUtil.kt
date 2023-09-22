package com.mtg.tool.findmyphone.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Handler

object MediaPlayerUtil {
    lateinit var mediaPlayer: MediaPlayer
    var isPlay = false
    var callback: (() -> Unit)? = null
    var handler = Handler()
    var stopRunnable = Runnable { stopAudio() }

    //    play audio no limits
    fun playAudioAssets(context: Context, name: String, callback: () -> Unit) {
        restartAudio()
        this.callback = callback
        val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd(name)
        mediaPlayer.setDataSource(
            assetFileDescriptor.fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        )
        mediaPlayer.setOnCompletionListener {
            stopAudio()
        }
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    fun playAudioPath(path: String, callback: () -> Unit) {
        restartAudio()
        this.callback = callback
        try {
            mediaPlayer.setDataSource(path)
            mediaPlayer.setOnCompletionListener {
                stopAudio()
            }
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //  play audio with timeStart and timeEnd
    fun playAudioPath(
        path: String, timeStart: Int, timeEnd: Int, callback: () -> Unit
    ) {
        restartAudio()
        try {
            this.callback = callback
            mediaPlayer.seekTo(timeStart)
            mediaPlayer.setDataSource(path)
            mediaPlayer.prepare()
            mediaPlayer.start()

            handler.postDelayed(stopRunnable, (timeEnd - timeStart).toLong())

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playAudioAssets(
        context: Context, name: String, timeStart: Int, timeEnd: Int, callback: () -> Unit
    ) {
        restartAudio()
        this.callback = callback
        val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd(name)
        mediaPlayer.setDataSource(
            assetFileDescriptor.fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        )
        handler.postDelayed(stopRunnable, (timeEnd - timeStart).toLong())
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
        callback?.invoke()
        callback = null
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