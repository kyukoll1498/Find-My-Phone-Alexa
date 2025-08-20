package com.alx.findphone.claptofind.flashalert.main.clap
import android.annotation.SuppressLint
import android.media.AudioRecord
import java.lang.Exception

class Recorder {
    private val audioEncoding = 2
    private var rateSupported = 0
    private var rateSend = false

    private val validSampleRates: Int
        get() {
            for (i in intArrayOf(44100, 22050, 16000, 11025, 8000)) {
                if (AudioRecord.getMinBufferSize(i, 1, 2) > 0 && !rateSend) {
                    rateSupported = i
                    rateSend = true
                }
            }
            return rateSupported
        }

    private val channelConfiguration = 16
    private val sampleRate = validSampleRates

    @SuppressLint("MissingPermission")
    private val audioRecord = AudioRecord(1, sampleRate, channelConfiguration, audioEncoding, AudioRecord.getMinBufferSize(sampleRate, channelConfiguration, audioEncoding))

    fun stopRecording() {
        try {
            audioRecord.stop()
            audioRecord.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
