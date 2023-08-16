package com.mtg.tool.findmyphone.utils

import android.media.MediaRecorder
import java.io.IOException


object RecordUtil {
    private var mediaRecorder = MediaRecorder()

    fun startRecord(outputPath: String) {
        startRecording(outputPath)
    }

    private fun startRecording(outputPath: String) {
        stopRecording()
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder.setOutputFile(outputPath)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        try {
            mediaRecorder.prepare()
            mediaRecorder.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun stopRecording() {
        try {
            mediaRecorder.stop()
            mediaRecorder.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun pauseRecording() {
        try {
            mediaRecorder.pause()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun resumeRecording() {
        try {
            mediaRecorder.resume()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}