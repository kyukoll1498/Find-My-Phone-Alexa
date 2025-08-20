package com.alx.findphone.claptofind.flashalert.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager

class VolumeChangeReceiver : BroadcastReceiver {
    private var audioManager: AudioManager
    private var listener: VolumeChangeListener

    constructor(context: Context, listener: VolumeChangeListener) {
        this.listener = listener
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val volume: Int = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (listener != null) {
            listener.onVolumeChanged(volume)
        }
    }

    interface VolumeChangeListener {
        fun onVolumeChanged(volume: Int)
    }
}