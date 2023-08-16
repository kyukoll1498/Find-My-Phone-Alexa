package com.mtg.tool.findmyphone.utils

import android.content.Context
import linc.com.library.AudioTool
import java.io.File


object AudioUtil {
    fun cutAudio( context: Context, start: Int, duration: Int, path: String, callback: (outputPath: String) -> Unit) {
        var outputPath = path
        AudioTool.getInstance(context)
            .withAudio(File(path))
            .cutAudio(start,duration) { callback.invoke(outputPath) }
            .saveCurrentTo(outputPath)
            .release()
    }
}