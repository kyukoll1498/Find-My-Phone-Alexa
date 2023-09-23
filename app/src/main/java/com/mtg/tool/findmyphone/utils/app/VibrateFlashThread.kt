package com.mtg.tool.findmyphone.utils.app

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mtg.tool.findmyphone.MODE_FLASH_DEFAULT
import com.mtg.tool.findmyphone.MODE_FLASH_DISCO
import com.mtg.tool.findmyphone.MODE_FLASH_SOS
import com.mtg.tool.findmyphone.MODE_VIBRATE_DEFAULT
import com.mtg.tool.findmyphone.MODE_VIBRATE_HEART
import com.mtg.tool.findmyphone.MODE_VIBRATE_STRONG
import com.mtg.tool.findmyphone.MODE_VIBRATE_TICKTOCK

class VibrateFlashThread :
    Thread {
    private var context: Context
    private var mode: Int = 0
    private var duration: Int = 0
    private var delay: Int = 0

    companion object {
        var isCancellable = true
        private lateinit var vibrator: Vibrator
        private lateinit var manager: CameraManager

        fun stopAll() {
            stopVibrate()
            stopFlash()
        }

        fun stopVibrate() {
            if (isRunningVibrate) {
                vibrator.cancel()
                isRunningVibrate = false
            }

        }

        fun stopFlash() {
            if (isRunningFlash) {
                val cameraId = manager.cameraIdList[0]
                cameraId?.let { manager.setTorchMode(it, false) }
                isRunningFlash = false
            }

        }

        const val VIBRATE_MODE = 124
        const val FLASH_MODE = 126
        var isRunningFlash = false
        var isRunningVibrate = false
        var durationSum = 3000
    }

    constructor(
        context: Context,
        modeCode: Int, durationSum: Int = 3000
    ) {
        this.context = context
        VibrateFlashThread.durationSum = durationSum
        when (modeCode) {
            MODE_FLASH_DEFAULT -> {
                this.mode = FLASH_MODE
                this.duration = durationSum
                this.delay = 0
            }

            MODE_FLASH_SOS -> {
                this.mode = FLASH_MODE
                this.duration = 600
                this.delay = 0
            }

            MODE_FLASH_DISCO -> {
                this.mode = FLASH_MODE
                this.duration = 100
                this.delay = 0
            }

            MODE_VIBRATE_DEFAULT -> {
                this.mode = VIBRATE_MODE
                this.duration = durationSum
                this.delay = 0
            }

            MODE_VIBRATE_STRONG -> {
                this.mode = VIBRATE_MODE
                this.duration = 1000
                this.delay = 1000
            }

            MODE_VIBRATE_HEART -> {
                this.mode = VIBRATE_MODE
                this.duration = 150
                this.delay = 700
            }

            MODE_VIBRATE_TICKTOCK -> {
                this.mode = VIBRATE_MODE
                this.duration = 100
                this.delay = 100
            }
        }

        when (mode) {
            VIBRATE_MODE -> {
                AppPreferences.instance.currentVibrate = modeCode
            }

            FLASH_MODE -> {
                AppPreferences.instance.currentFlash = modeCode
            }
        }

    }

    override fun run() {
        if (mode == FLASH_MODE) {
            if (isRunningFlash) {
                stopFlash()
                sleep(100)
                doAction()
            } else {
                doAction()
            }
        } else {
            if (isRunningVibrate) {
                stopVibrate()
                sleep(100)
                doAction()
            } else {
                doAction()
            }
        }

    }

    private fun doAction() {
        if (mode == VIBRATE_MODE) {
            isRunningVibrate = true
            vibrate()
        } else if (mode == FLASH_MODE) {
            isRunningFlash = true
            switchFlash()
        }
    }

    private fun switchFlash() {
        manager = context.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager
        val cameraId = manager.cameraIdList[0]
        try {
            if (durationSum != duration) {
                for (i in 0 until durationSum / 2 / duration) {
                    cameraId?.let { manager.setTorchMode(it, true) }
                    if (!startSleep(duration)) {
                        cameraId?.let { manager.setTorchMode(it, false) }
                        return
                    }
                    cameraId?.let { manager.setTorchMode(it, false) }
                    if (!startSleep(duration)) {
                        cameraId?.let { manager.setTorchMode(it, false) }
                        return
                    }
                }
            } else {
                cameraId?.let { manager.setTorchMode(it, true) }
                if (!startSleep(duration)) {
                    cameraId?.let { manager.setTorchMode(it, false) }
                    return
                }
            }

        } catch (e: CameraAccessException) {
            e.printStackTrace()
            cameraId?.let { manager.setTorchMode(it, false) }
        } catch (e: InterruptedException) {
            cameraId?.let { manager.setTorchMode(it, false) }
            throw RuntimeException(e)
        } catch (e: Exception) {
            cameraId?.let { manager.setTorchMode(it, false) }
        }
    }

    private fun vibrate() {
        vibrator = (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?)!!
        try {
            if (delay == 0) {
                for (i in 0..durationSum / 1000) {
                    vibrator.vibrate(1000)
                    if (!startSleep(1000)) {
                        return
                    }
                }
            } else {
                for (i in 0..durationSum / (duration + delay)) {
                    vibrator.vibrate(duration.toLong())
                    if (!startSleep(duration + delay)) {
                        return
                    }

                }
            }
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
    }

    private fun startSleep(i: Int): Boolean {
        for (j in 0 until i / 50) {
            sleep(50)
            if (mode == FLASH_MODE) {
                if (!isRunningFlash) {
                    return false
                }
            } else {
                if (!isRunningVibrate) {
                    return false
                }
            }

        }
        return true
    }
}