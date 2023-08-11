package com.mtg.tool.findmyphone.data.repo

import android.content.Context
import com.mtg.tool.findmyphone.DEFAULT_SOUND_TYPE
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.data.model.SoundItem

object AppRepository {
    fun getAllSound(context: Context): List<SoundItem> {
        return arrayListOf(
            SoundItem(
                DEFAULT_SOUND_TYPE,
                "Cat Meowing",
                0,
                R.drawable.image_sound_1,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                "Cat Meowing",
                0,
                R.drawable.image_sound_2,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                "Cat Meowing",
                0,
                R.drawable.image_sound_3,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                "Cat Meowing",
                0,
                R.drawable.image_sound_4,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                "Cat Meowing",
                0,
                R.drawable.image_sound_5,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                "Cat Meowing",
                0,
                R.drawable.image_sound_6,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                "Cat Meowing",
                0,
                R.drawable.image_sound_7,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                "Cat Meowing",
                0,
                R.drawable.image_sound_8,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                "Cat Meowing",
                0,
                R.drawable.image_sound_9,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                "Cat Meowing",
                0,
                R.drawable.image_sound_10,
                "file:///android_asset/cat_meowing.mp3"
            )
        )
    }
}