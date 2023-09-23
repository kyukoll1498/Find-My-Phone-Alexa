package com.mtg.tool.findmyphone.main.clap

import android.R.attr.data
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.res.AssetFileDescriptor
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mtg.tool.findmyphone.DEFAULT_SOUND_TYPE
import com.mtg.tool.findmyphone.IMPORT_SOUND_TYPE
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import com.mtg.tool.findmyphone.utils.app.VibrateFlashThread
import java.io.IOException


class FeatureClapManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var isVibrating = false
    private var appPreferences = AppPreferences.instance
    private val handler = Handler(Looper.getMainLooper())
    private var runnable = Runnable {
        VibrateFlashThread.isCancellable = true
        stopSound()
        VibrateFlashThread.stopAll()
        callback.invoke(true)
    }
    private val audioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    private lateinit var callback: (Boolean) -> Unit

    private fun playSoundSaveGson() {
        val statusPlay = AppPreferences.instance.hasSound
        playSound(appPreferences.currentDuration.toLong(), statusPlay)
    }

    private fun setVolume(){
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, appPreferences.currentVolume, 0);
    }

    private fun playSound(duration: Long, statusPlay: Boolean) {
        if (!statusPlay) {
            return
        }
        if (mediaPlayer == null) mediaPlayer = MediaPlayer()
        setVolume()
        try {
            val currentSoundItem = appPreferences.currentSound
            when (currentSoundItem.type) {
                IMPORT_SOUND_TYPE -> {
                    mediaPlayer?.setDataSource(currentSoundItem.soundPath)
                }
                DEFAULT_SOUND_TYPE -> {
                    val assetFileDescriptor: AssetFileDescriptor = context.assets.openFd(currentSoundItem.soundPath!!.substring(currentSoundItem.soundPath!!.lastIndexOf("/") + 1))
                    mediaPlayer!!.setDataSource(
                        assetFileDescriptor.fileDescriptor,
                        assetFileDescriptor.startOffset,
                        assetFileDescriptor.length
                    )
                }
            }
            mediaPlayer!!.isLooping = true
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()

        } catch (e: IOException) {
            Log.d("error in opening audio file", e.toString())
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            Log.d("MediaPlayer has a status error", e.toString())
            e.printStackTrace()
        } catch (e: Exception) {
            Log.d("Other error", e.toString())
            e.printStackTrace()
        }
    }

    fun stopSound() {
        if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    fun flashSaveGson() {
        val statusFlash = AppPreferences.instance.hasFlash
        turnOnFlash(appPreferences.currentDuration.toLong(), statusFlash)
    }

    private fun turnOnFlash(duration: Long, statusFlash: Boolean) {
        if (!statusFlash) {
            turnOffFlash()
            return
        }
        val manager = context.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager
        val cameraId: String?
        try {
            cameraId = manager.cameraIdList[0]

            // Check flash of camera
            val cameraCharacteristics = manager.getCameraCharacteristics(cameraId)
            if (!cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)!!) {
                Toast.makeText(context, "Your Flash is error",Toast.LENGTH_SHORT).show()
                return
            }

            manager.setTorchMode(cameraId, true)
            VibrateFlashThread(context, AppPreferences(context).currentFlash, duration.toInt()).start()

            handler.postDelayed({
                manager.setTorchMode(cameraId, false)
            }, duration)
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    private fun turnOffFlash() {
        val manager = context.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager
        val cameraId: String?
        try {
            cameraId = manager.cameraIdList[0]
            manager.setTorchMode(cameraId, false)
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    private fun vibrationSaveGson() {
        val statusFlash = AppPreferences.instance.hasVibrate
        turnOnVibration(appPreferences.currentDuration.toLong(), statusFlash)
    }

    private fun turnOnVibration(duration: Long, statusVibration: Boolean) {
        if (!statusVibration) {
            turnOffVibration()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrateMode = AppPreferences(context).currentVibrate
            val vibrateFlashThread = VibrateFlashThread(context, vibrateMode, duration.toInt())
            vibrateFlashThread.start()
        }
    }

    private fun turnOffVibration() {
        VibrateFlashThread.stopAll()
        val vibrator = context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.cancel()
        isVibrating = false
    }

    fun playAll() {
        handler.postDelayed(runnable, appPreferences.currentDuration.toLong())
        VibrateFlashThread.isCancellable = false
        vibrationSaveGson()
        flashSaveGson()
        playSoundSaveGson()
    }

    fun setCallback(callback: (Boolean) -> Unit) {
        this.callback = callback
    }

    fun stopAll() {
        try {
            handler.removeCallbacks(runnable)
            callback.invoke(false)
            VibrateFlashThread.isCancellable = true
            turnOffFlash()
            turnOffVibration()
            stopSound()
        } catch (e: Exception) {
             e.printStackTrace()
        }

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
