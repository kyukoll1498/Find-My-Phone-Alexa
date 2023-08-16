package com.mtg.tool.findmyphone.data.repo

import android.content.Context
import com.mtg.tool.findmyphone.ADS_SOUND_TYPE
import com.mtg.tool.findmyphone.DEFAULT_SOUND_TYPE
import com.mtg.tool.findmyphone.IMPORT_SOUND_TYPE
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.data.db.RoomDatabase
import com.mtg.tool.findmyphone.data.model.SoundItem

object AppRepository {
    fun getAllSound(context: Context): List<SoundItem> {
        return arrayListOf(
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.cat_meowing),
                0,
                R.drawable.image_sound_1,
                R.drawable.avatar_sound_1,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.dog_barking),
                0,
                R.drawable.image_sound_2,
                R.drawable.avatar_sound_2,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.hey_stay_here),
                0,
                R.drawable.image_sound_3,
                R.drawable.avatar_sound_3,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.whistle),
                0,
                R.drawable.image_sound_4,
                R.drawable.avatar_sound_4,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.hello),
                0,
                R.drawable.image_sound_5,
                R.drawable.avatar_sound_5,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.car_horn),
                0,
                R.drawable.image_sound_6,
                R.drawable.avatar_sound_6,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(ADS_SOUND_TYPE),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.door_bell),
                0,
                R.drawable.image_sound_7,
                R.drawable.avatar_sound_7,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.party_horn),
                0,
                R.drawable.image_sound_8,
                R.drawable.avatar_sound_8,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.police_whistle),
                0,
                R.drawable.image_sound_9,
                R.drawable.avatar_sound_9,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.trumpet),
                0,
                R.drawable.image_sound_10,
                R.drawable.avatar_sound_10,
                "file:///android_asset/cat_meowing.mp3"
            )
        )
    }

    fun insertSound(soundItem: SoundItem) {
        RoomDatabase.getDatabase()?.soundDao()?.insert(soundItem)
    }
    fun deleteSound(soundItem: SoundItem) {
        soundItem.soundPath?.let { RoomDatabase.getDatabase()?.soundDao()?.delete(it) }
    }
}