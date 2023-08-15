package com.mtg.tool.findmyphone.main.clap

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class RestartServiceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "YourServiceRestartAction") {
            val serviceIntent = Intent(context, VocalService::class.java)
            context.startService(serviceIntent)
        }
    }
}