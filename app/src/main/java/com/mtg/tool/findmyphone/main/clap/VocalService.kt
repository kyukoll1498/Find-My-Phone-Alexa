package com.mtg.tool.findmyphone.main.clap

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.main.fragment.HomeFragment

class VocalService : Service() {
    private var classesApp: ClassesApp? = null
    private var recorderThread: RecorderThread? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startDetection()
        val notification = buildNotification()
        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startDetection() {
        try {
            DetectClapClap(applicationContext, object : IDetect {
                override fun onDetected() {
                    val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(4000)
                    turnOnFlash(true)
                    Handler(Looper.getMainLooper()).postDelayed({ handleOff() }, (1000 * 10).toLong())
                    playAudioAssets()
                    Log.e("~~~", "onDetected: ")
                }
            }).listen()
            classesApp = ClassesApp(this)
            classesApp!!.save("detectClap", "0")
        } catch (unused: Exception) {
            Toast.makeText(this, "Recorder not supported by this device", Toast.LENGTH_LONG).show()
        }
    }

    var mediaPlayer: MediaPlayer? = null
    fun handleOff() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
        }
        turnOnFlash(false)
    }

    fun playAudioAssets() {
        if (mediaPlayer == null) mediaPlayer = MediaPlayer()
        try {
            val assetFileDescriptor = assets.openFd("cat_meowing.mp3")
            mediaPlayer!!.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
            mediaPlayer!!.isLooping = true
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
        } catch (e: Exception) {
        }
    }

    private fun turnOnFlash(isOn: Boolean) {
        val manager = this.getSystemService(AppCompatActivity.CAMERA_SERVICE) as CameraManager
        var cameraId: String? = null
        try {
            cameraId = manager.cameraIdList[0]
            manager.setTorchMode(cameraId, isOn)
        } catch (e: CameraAccessException) {
            throw RuntimeException(e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val recorderThread = recorderThread
        if (recorderThread != null) {
            recorderThread.stopRecording()
            this.recorderThread = null
        }
        selectedDetection = 0
        Toast.makeText(this, "Detection stopped", Toast.LENGTH_LONG).show()
    }

    fun onWhistleDetected() {
        val intent = Intent(this, HomeFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        Toast.makeText(this, "Clap detected", Toast.LENGTH_LONG).show()
        stopSelf()
    }

    private fun buildNotification(): Notification {
        val fullScreenIntent = Intent(this, HomeFragment::class.java)
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, flag)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder: Notification.Builder = Notification.Builder(this)
            .setSmallIcon(R.drawable.flag_vi)
            .setContentTitle("Battery charging animation")
            .setPriority(Notification.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
        //                        .setFullScreenIntent(fullScreenPendingIntent, true);
        notificationBuilder.setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(NotificationChannel("123", "123", NotificationManager.IMPORTANCE_HIGH))
            notificationBuilder.setChannelId("123")
        }
        return notificationBuilder.build()
    }

    companion object {
        const val DETECT_NONE = 0
        const val DETECT_WHISTLE = 1
        private const val NOTIFICATION_Id = 1
        var selectedDetection = 0
    }
}