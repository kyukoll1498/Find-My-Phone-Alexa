package com.alx.findphone.claptofind.flashalert.utils

import android.content.Context
import android.media.AudioManager

object AudioMangerUtils {
    fun getMaxVolume(context: Context): Int {
        var audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }
    fun getDefaultVolume(context: Context): Int {
        return getMaxVolume(context) * 7/10
    }
}