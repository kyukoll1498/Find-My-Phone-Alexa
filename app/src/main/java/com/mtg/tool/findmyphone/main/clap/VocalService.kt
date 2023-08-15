package com.mtg.tool.findmyphone.main.clap

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Vibrator
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.main.fragment.HomeFragment

class VocalService : Service() {
    private var classesApp: ClassesApp? = null
    private var recorderThread: RecorderThread? = null
    private var notificationChannel: NotificationChannel? = null
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
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
        } catch (_: Exception) {
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

    private fun buildNotification(): Notification {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this.applicationContext, HomeFragment::class.java)
        val pendingIntent = PendingIntent.getActivity(this.applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val contentView = RemoteViews(this.packageName, R.layout.popup_notification)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel?.enableLights(true)
            notificationChannel?.lightColor = Color.GREEN
            notificationChannel?.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel!!)
        }

        val notificationBuilder = NotificationCompat.Builder(this.applicationContext, channelId)
            .setContent(contentView)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background))
            .setContentIntent(pendingIntent)

        notificationManager.notify(1234, notificationBuilder.build())

        return notificationBuilder.build()
    }

    companion object {
        const val DETECT_NONE = 0
        const val DETECT_WHISTLE = 1
        private const val NOTIFICATION_Id = 1
        var selectedDetection = 0
    }
}