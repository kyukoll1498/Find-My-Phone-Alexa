package com.mtg.tool.findmyphone.main.clap

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioRecord
import android.media.MediaPlayer
import android.os.Vibrator
import be.hogent.tarsos.dsp.AudioEvent
import be.hogent.tarsos.dsp.AudioFormat
import be.hogent.tarsos.dsp.onsets.OnsetHandler
import be.hogent.tarsos.dsp.onsets.PercussionOnsetDetector
import com.mtg.tool.findmyphone.R

class DetectClapClap @SuppressLint("MissingPermission") internal constructor(context: Context, mCallback: IDetect) : OnsetHandler {
    private var run = false
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
    private var v: Vibrator? = null
    private val callback: IDetect

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
        val nb_claps = 2
        if (clap >= nb_claps) {
            classesApp.save("detectClap", "1")
            mIsRecording = false
            callback.onDetected()
        }
    }

    private fun clearNotification() {
        val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1234)
    }

    fun listen() {
        recorder.startRecording()
        torsosFormat = AudioFormat(SAMPLE_RATE.toFloat(), 16, 1, true, false)
        Thread {
            while (mIsRecording) {
                val audioEvent = AudioEvent(
                    torsosFormat,
                    recorder.read(buffer, 0, buffer.size).toLong()
                )
                audioEvent.setFloatBufferWithByteBuffer(buffer)
                mPercussionOnsetDetector.process(audioEvent)
            }
            recorder.stop()
        }.start()
    }

    private fun runVibrate() {
        val v = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        Thread {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                try {
                    v.vibrate(1000)
                } catch (ignored: Exception) {
                }
            }
        }.start()
    }

    private fun runVibrate(z: Boolean) {
        run = z
        v = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        Thread {
            while (true) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                try {
                    v!!.vibrate(1000)
                } catch (ignored: Exception) {
                }
            }
        }.start()
    }

    private fun runSong() {
        mySong = MediaPlayer.create(mContext, R.raw.cat_meowing)
        mySong!!.setOnCompletionListener { mediaPlayer: MediaPlayer? -> runSong() }
        mySong!!.start()
    }

//    private fun turnOnFlash() {
//        if (!isFlashOn) {
//            val camera = camera
//            if (camera != null && params != null) {
//                isFlashOn = true
//                try {
//                    params = camera.parameters
//                    params.setFlashMode("torch")
//                    camera.parameters = params
//                    camera.startPreview()
//                } catch (ignored: Exception) {
//                }
//            }
//        }
//    }

//    fun turnOffFlash() {
//        if (isFlashOn) {
//            val camera = camera
//            if (camera != null && params != null) {
//                isFlashOn = false
//                try {
//                    params = camera.parameters
//                    params.setFlashMode("off")
//                    camera.parameters = params
//                    camera.stopPreview()
//                } catch (ignored: Exception) {
//                }
//            }
//        }
//    }

    companion object {
        var mySong: MediaPlayer? = null
        var SAMPLE_RATE = 8000
    }
}