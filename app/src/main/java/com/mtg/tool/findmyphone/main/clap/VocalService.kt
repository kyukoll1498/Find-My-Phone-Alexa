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
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.main.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class VocalService : Service() {
    private var classesApp: ClassesApp? = null
    private var recorderCoroutine: RecorderCoroutine? = null
    private var notificationChannel: NotificationChannel? = null
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startDetection()
        //Cancel Notification
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1234)

        val notification = buildNotification()
        startForeground(1234, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startDetection() {
        try {
            DetectClapClap(applicationContext, object : IDetect {
                override fun onDetected() {
                    Log.d("ClapCount","1")
                    FeatureClapManager.getInstance(applicationContext).vibrate(30000)
                    FeatureClapManager.getInstance(applicationContext).turnOnFlash(true)
                    FeatureClapManager.getInstance(applicationContext).playAudio()
                }
            }).listen()
            classesApp = ClassesApp(this)
            classesApp!!.save("detectClap", "0")
        } catch (unused: Exception) {
            Toast.makeText(this, "Recorder not supported by this device", Toast.LENGTH_LONG).show()
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        val recorderThread = recorderThread
//        if (recorderThread != null) {
//            recorderThread.stopRecording()
//            this.recorderThread = null
//        }
//        selectedDetection = 0
//        Toast.makeText(this, "Detection stopped", Toast.LENGTH_LONG).show()
//    }

    override fun onDestroy() {
        super.onDestroy()
        val recorderThread = recorderCoroutine
        if (recorderThread != null) {
            CoroutineScope(Dispatchers.IO).launch {
                recorderThread.stopRecording()
            }
            this.recorderCoroutine = null
        }
        selectedDetection = 0
        Toast.makeText(this, "Detection stopped", Toast.LENGTH_LONG).show()
    }

    private fun buildNotification(): Notification {
        try {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val intentClickNotification = Intent(this, MainActivity::class.java)
            intentClickNotification.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingClickNo = PendingIntent.getActivity(this, 0, intentClickNotification, PendingIntent.FLAG_IMMUTABLE)

            val intentDisplayHome = Intent(this, MainActivity::class.java)
            intentDisplayHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            intentDisplayHome.putExtra("VIEWPAGER_POSITION", 0)
            val pendingIntentDisplayHome = PendingIntent.getActivity(this, 0, intentDisplayHome, PendingIntent.FLAG_IMMUTABLE)

            val contentView = RemoteViews(this.packageName, R.layout.popup_notification)

            val notificationBuilder: NotificationCompat.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel?.enableLights(true)
                notificationChannel?.lightColor = Color.GREEN
                notificationChannel?.enableVibration(false)
                notificationManager.createNotificationChannel(notificationChannel!!)
                notificationBuilder = NotificationCompat.Builder(this.applicationContext, channelId)
            } else {
                notificationBuilder = NotificationCompat.Builder(this.applicationContext)
            }
            contentView.setOnClickPendingIntent(R.id.notification_layout, pendingClickNo)
            contentView.setOnClickPendingIntent(R.id.notification_layout, pendingIntentDisplayHome)
            notificationBuilder.setContent(contentView)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background))
                .setContentIntent(pendingClickNo)
                .setAutoCancel(true)

            val notification = notificationBuilder.build()
            try {
                notificationManager.notify(1234, notification)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return notification
        } catch (e: Exception) {
            // Handle any other exceptions that might occur during the notification creation
            e.printStackTrace()
        }

        // Return a default notification in case of failure
        return NotificationCompat.Builder(this.applicationContext, channelId)
            .setContentTitle("Notification Error")
            .setContentText("An error occurred while creating the notification.")
            .setSmallIcon(R.drawable.icon_app_border)
            .build()
    }

    companion object {
        var selectedDetection = 0
    }
}