package com.mtg.tool.findmyphone.main.clap

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioRecord
import android.util.Log
import be.hogent.tarsos.dsp.AudioEvent
import be.hogent.tarsos.dsp.AudioFormat
import be.hogent.tarsos.dsp.onsets.OnsetHandler
import be.hogent.tarsos.dsp.onsets.PercussionOnsetDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@SuppressLint("MissingPermission")
class DetectClapClap internal constructor(context: Context, mCallback: IDetect) : OnsetHandler {
    private val buffer: ByteArray
    private var clap: Int
    private val classesApp: ClassesApp
    private val mContext: Context
    private var mIsRecording: Boolean
    private val mPercussionOnsetDetector: PercussionOnsetDetector
    private var rateSupported = 0
    private var rateSend = false
    private val recorder: AudioRecord
    private var torsosFormat: AudioFormat? = null
    private val callback: IDetect
    private var listenThread: Thread? = null

    init {
        classesApp = ClassesApp(context)
        SAMPLE_RATE = validSampleRates
        mContext = context
        val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, 16, 2)
        buffer = ByteArray(minBufferSize)
        recorder = AudioRecord(1, SAMPLE_RATE, 16, 2, minBufferSize)
        mPercussionOnsetDetector = PercussionOnsetDetector(SAMPLE_RATE.toFloat(), minBufferSize / 2, this, 24.0, 5.0)
        clap = 0
        mIsRecording = true
        callback = mCallback
    }

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

    override fun handleOnset(d: Double, d2: Double) {
        clap++
        val nbClaps = 2
        if (clap >= nbClaps) {
            classesApp.save("detectClap", "1")
            mIsRecording = false
            callback.onDetected()
        }
    }

    open fun continueRecord() {
        mIsRecording = true
        listen()
    }
    open fun stopRecord() {
        mIsRecording = false
    }

    val TAG = "~~~"
    private var listenJob: Job? = null

    fun listen() {
        if (listenJob != null && listenJob!!.isActive) {
            listenJob!!.cancel()
            return // Return if the job is already running
        }

        listenJob = CoroutineScope(Dispatchers.IO).launch {
            recorder.startRecording()
            val torsosFormat = AudioFormat(SAMPLE_RATE.toFloat(), 16, 1, true, false)

            while (mIsRecording) {
                val audioEvent = AudioEvent(
                    torsosFormat,
                    recorder.read(buffer, 0, buffer.size).toLong()
                )
                audioEvent.setFloatBufferWithByteBuffer(buffer)
                mPercussionOnsetDetector.process(audioEvent)
                Log.e(TAG, "listen: inside while loop")
            }

            recorder.stop()
            Log.e(TAG, "listen: stop recorder")
        }
    }

    companion object {
        var SAMPLE_RATE = 8000
    }
}