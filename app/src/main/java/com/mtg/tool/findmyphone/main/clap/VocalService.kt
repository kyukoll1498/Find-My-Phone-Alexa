package com.mtg.tool.findmyphone.main.clap

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.mtg.tool.findmyphone.ACTION_NOTIFICATION_CLICKED_SERVICE
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.main.activity.MainActivity
import java.util.Locale


@Suppress("DEPRECATION")
class VocalService : Service() {
    private var classesApp: ClassesApp? = null
    private var recorder: Recorder? = null
    private var notificationChannel: NotificationChannel? = null
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"
    private val handler = Handler()
    private val serviceDurationMillis = 60 * 60 * 1000 // 1 hour in milliseconds

    private lateinit var detectClapClap: DetectClapClap


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_NOT_STICKY
        }

        // Cancel notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1234)

        val notification = buildNotification()
        startForeground(1234, notification)

        if (intent.action == ACTION_NOTIFICATION_CLICKED_SERVICE) {
            performNotificationAction()
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        }

        //set limit service run in 1 hour
        handler.postDelayed({
            stopForeground(true)
            stopSelf()
        }, serviceDurationMillis.toLong())

        startDetection()

        return START_STICKY
    }


    private fun performNotificationAction() {
        val featureClapManager = FeatureClapManager.getInstance(this)
        featureClapManager.stopAll()
        stopSelf()
    }

    private fun startDetection() {
        try {
            FeatureClapManager.getInstance(applicationContext).setCallback {
                if (it) {
                    restartDetection()
                } else {
                    stopDetection()
                }
            }
            detectClapClap = DetectClapClap(applicationContext, object : IDetect {
                override fun onDetected() {
                    Log.d("ClapCount", "1")
                    FeatureClapManager.getInstance(applicationContext).playAll()

                }
            })
            detectClapClap.listen()
            classesApp = ClassesApp(this)
            classesApp!!.save("detectClap", "0")
        } catch (unused: Exception) {
            Toast.makeText(this, "Recorder not supported by this device", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopDetection() {
        detectClapClap.stopRecord()
    }

    private fun restartDetection() {
        detectClapClap.continueRecord()
    }

    override fun onDestroy() {
        super.onDestroy()
        val recorderThread = recorder
        if (recorderThread != null) {
            recorderThread.stopRecording()
            this.recorder = null
        }
        selectedDetection = 0
//        Toast.makeText(this, "Detection stopped", Toast.LENGTH_LONG).show()
        FeatureClapManager.getInstance(this).stopAll()
        handler.removeCallbacksAndMessages(null)
    }

    @SuppressLint("LaunchActivityFromNotification")
    private fun buildNotification(): Notification {
        try {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val intentNotificationAction = Intent(this, MainActivity::class.java)
            intentNotificationAction.action = ACTION_NOTIFICATION_CLICKED_SERVICE
            val pendingIntentNotificationAction = PendingIntent.getActivity(
                this,
                0,
                intentNotificationAction,
                PendingIntent.FLAG_IMMUTABLE
            )

            val contentView = RemoteViews(this.packageName, R.layout.popup_notification)

            val notificationBuilder: NotificationCompat.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel =
                    NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel?.enableLights(true)
                notificationChannel?.lightColor = Color.GREEN
                notificationChannel?.enableVibration(false)
                notificationManager.createNotificationChannel(notificationChannel!!)
                notificationBuilder = NotificationCompat.Builder(this.applicationContext, channelId)
            } else {
                notificationBuilder = NotificationCompat.Builder(this.applicationContext)
            }

            contentView.setOnClickPendingIntent(
                R.id.notification_layout,
                pendingIntentNotificationAction
            )

            val smallIcon = if (Build.BRAND.equals("Google", ignoreCase = true) &&
                (Build.MODEL.startsWith("Pixel") || Build.DEVICE.startsWith("pixel"))
            ) {
                R.drawable.ic_icon_noti_pixel
            } else {
                R.drawable.ic_icon_app_small
            }

            notificationBuilder.setContent(contentView)
                .setCustomContentView(contentView)
                .setSmallIcon(smallIcon)
                .setCustomBigContentView(contentView)
                .setCustomHeadsUpContentView(contentView)
                .setContentIntent(pendingIntentNotificationAction)
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
            .setSmallIcon(R.drawable.ic_icon_app_small)
            .build()
    }

    companion object {
        var selectedDetection = 0
    }
}