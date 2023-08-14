package com.mtg.tool.findmyphone.utils.app

import android.content.Context
import com.mtg.tool.findmyphone.DEFAULT_SOUND_TYPE
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.utils.MediaPlayerUtil

object MediaPlayerAppUtil{
    fun playAudio(context: Context, soundItem: SoundItem, callback: () -> Unit) {
        if (soundItem.type == DEFAULT_SOUND_TYPE) {
            soundItem.soundPath?.let { MediaPlayerUtil.playAudioAssets(context, it.substring(it.lastIndexOf("/") + 1),0, callback) }
        } else {
            soundItem.soundPath?.let { MediaPlayerUtil.playAudioPath(it,0, callback) }
        }
    }

    fun stopAudio() {
        MediaPlayerUtil.stopAudio()
    }
}