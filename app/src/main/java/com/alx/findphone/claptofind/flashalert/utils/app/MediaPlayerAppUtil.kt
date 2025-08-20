package com.alx.findphone.claptofind.flashalert.utils.app

import android.content.Context
import com.alx.findphone.claptofind.flashalert.DEFAULT_SOUND_TYPE
import com.alx.findphone.claptofind.flashalert.data.model.SoundItem
import com.alx.findphone.claptofind.flashalert.utils.MediaPlayerUtil

object MediaPlayerAppUtil {
    fun playAudio(context: Context, soundItem: SoundItem, callback: () -> Unit) {
        if (soundItem.type == DEFAULT_SOUND_TYPE) {
            soundItem.soundPath?.let {
                MediaPlayerUtil.playAudioAssets(
                    context,
                    it.substring(it.lastIndexOf("/") + 1),
                    callback
                )
            }
        } else {
            soundItem.soundPath?.let { MediaPlayerUtil.playAudioPath(it, callback) }
        }
    }

    fun stopAudio() {
        MediaPlayerUtil.stopAudio()
    }
}