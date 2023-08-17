package com.mtg.tool.findmyphone.main.clap

import android.annotation.SuppressLint
import android.media.AudioRecord

//class RecorderThread : Thread() {
//    private val audioEncoding = 2
//    private var rateSupported = 0
//    private var rateSend = false
//
//    private val validSampleRates: Int
//        get() {
//            for (i in intArrayOf(44100, 22050, 16000, 11025, 8000)) {
//                if (AudioRecord.getMinBufferSize(i, 1, 2) > 0 && !rateSend) {
//                    rateSupported = i
//                    rateSend = true
//                }
//            }
//            return rateSupported
//        }
//
//    private val channelConfiguration = 16
//    private val sampleRate = validSampleRates
//
//    @SuppressLint("MissingPermission")
//    private val audioRecord = AudioRecord(1, sampleRate, channelConfiguration, audioEncoding, AudioRecord.getMinBufferSize(sampleRate, channelConfiguration, audioEncoding))
//
//    private fun startRecording() {
//        try {
//            audioRecord.startRecording()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    fun stopRecording() {
//        try {
//            audioRecord.stop()
//            audioRecord.release()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    override fun run() {
//        startRecording()
//    }
//}

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecorderCoroutine {
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

    fun startRecording() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                audioRecord.startRecording()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun stopRecording() {
        withContext(Dispatchers.IO) {
            try {
                audioRecord.stop()
                audioRecord.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
