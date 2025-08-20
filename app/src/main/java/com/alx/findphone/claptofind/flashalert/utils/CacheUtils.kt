package com.alx.findphone.claptofind.flashalert.utils

import android.content.Context
import com.alx.findphone.claptofind.flashalert.DIRECTION_AUDIO_CACHE_NAME
import java.io.File

object CacheUtils {
    private var lastAudioFilePath = ""

    fun getNewNameFileAudio(context: Context): String {
        if (!File(context.filesDir.path + "/" + DIRECTION_AUDIO_CACHE_NAME).exists()) {
            File(context.filesDir.path + "/" + DIRECTION_AUDIO_CACHE_NAME).mkdirs()
        }
        var extension = ".mp3"
        var newAudioPath = context.filesDir.path + "/" + DIRECTION_AUDIO_CACHE_NAME + "/audio"
        var i = 0;
        while (File(newAudioPath + extension).exists()) {
            i++
            newAudioPath += i
        }
        lastAudioFilePath = newAudioPath + extension
        return lastAudioFilePath
    }

    fun removeLastFileAudio() {
        if (lastAudioFilePath != "" && File(lastAudioFilePath).exists()) {
            File(lastAudioFilePath).delete()
        }
    }
    fun getLastFilePathAudio(): String {
        return lastAudioFilePath
    }
}