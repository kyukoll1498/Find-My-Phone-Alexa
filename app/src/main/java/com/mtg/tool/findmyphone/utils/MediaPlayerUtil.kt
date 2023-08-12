package com.mtg.tool.findmyphone.utils

import android.content.Context
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer

object MediaPlayerUtil {
    private lateinit var mediaPlayer: MediaPlayer
    fun playAudioAssets(context: Context, name: String) {
        mediaPlayer = MediaPlayer()
        val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd(name)
        mediaPlayer.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }

    fun playAudioPath(path: String) {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(path)
        mediaPlayer.prepare()
        mediaPlayer.start()
    }
}