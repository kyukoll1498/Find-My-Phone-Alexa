package com.mtg.tool.findmyphone.main.clap

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.res.AssetFileDescriptor
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Vibrator
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import com.mtg.tool.findmyphone.DEFAULT_SOUND_TYPE
import com.mtg.tool.findmyphone.IMPORT_SOUND_TYPE
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import com.mtg.tool.findmyphone.utils.app.VibrateFlashThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class FeatureClapManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var isVibrating = false
    private var appPreferences = AppPreferences.instance
    private val audioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    fun playSoundSaveGson() {
        val statusPlay = AppPreferences.instance.hasSound
        playSound(appPreferences.currentDuration.toLong(), statusPlay)
    }

    fun setVolume(){
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, appPreferences.currentVolume, 0);
    }

    private fun playSound(duration: Long, statusPlay: Boolean) {
        if (!statusPlay) {
            stopSound()
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
//            val assetFileDescriptor = context.assets.openFd("cat_meowing.mp3")
//            mediaPlayer!!.setDataSource(
//                assetFileDescriptor.fileDescriptor,
//                assetFileDescriptor.startOffset,
//                assetFileDescriptor.length
//            )
            mediaPlayer!!.isLooping = true
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()

            CoroutineScope(Dispatchers.Main).launch {
                delay(duration)
                stopSound()
            }
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
            manager.setTorchMode(cameraId, true)
            VibrateFlashThread(context, AppPreferences(context).currentFlash, duration.toInt()).start()
            CoroutineScope(Dispatchers.Main).launch {
                manager.setTorchMode(cameraId, false)
            }
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    fun turnOffFlash() {
        val manager = context.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager
        val cameraId: String?
        try {
            cameraId = manager.cameraIdList[0]
            manager.setTorchMode(cameraId, false)
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    fun vibrationSaveGson() {
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
            val coroutineScope = CoroutineScope(Dispatchers.Default)
            coroutineScope.launch {
                try {
                    val vibrateFlashThread = VibrateFlashThread(context, vibrateMode, duration.toInt())
                    vibrateFlashThread.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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
