package com.mtg.tool.findmyphone.main.clap

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import com.mtg.tool.findmyphone.utils.MediaPlayerUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import com.mtg.tool.findmyphone.utils.app.VibrateFlashThread
import kotlinx.coroutines.delay

class FeatureClapManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var isVibrating = false
    fun playAudio(duration: Long) {
        if (mediaPlayer == null) mediaPlayer = MediaPlayer()
        try {
            val assetFileDescriptor = context.assets.openFd("cat_meowing.mp3")
            mediaPlayer!!.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
            mediaPlayer!!.isLooping = true
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()

            CoroutineScope(Dispatchers.Main).launch {
                delay(duration)
                stopSound()
            }
        } catch (_: Exception) {
            // Handle exception here
        }
    }
    fun stopSound() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }
    fun turnOnFlash(duration: Long) {
        val manager = context.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager
        val cameraId: String?
        try {
            cameraId = manager.cameraIdList[0]
            manager.setTorchMode(cameraId, true)
            VibrateFlashThread(context, AppPreferences(context).currentFlash).start()
            CoroutineScope(Dispatchers.Main).launch {
                kotlinx.coroutines.delay(duration)
                manager.setTorchMode(cameraId, false)
            }
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    fun turnOffFlash(){
        val manager = context.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager
        val cameraId: String?
        try {
            cameraId = manager.cameraIdList[0]
            manager.setTorchMode(cameraId,false)
        } catch (e:CameraAccessException){
            throw RuntimeException(e)
        }
    }

    fun turnOnVibration(duration: Long) {
        val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrateFlashThread(context, AppPreferences(context).currentVibrate).start()
        } else {
            vibrator.vibrate(duration)
        }
    }

    fun turnOffVibration() {
        VibrateFlashThread.stopAll()
        val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
        isVibrating = false
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: FeatureClapManager? = null

        fun getInstance(context: Context): FeatureClapManager {
            if (instance == null) {
                instance = FeatureClapManager(context)
            }
            return instance!!
        }
    }
}
