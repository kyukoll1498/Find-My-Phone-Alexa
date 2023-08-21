package com.mtg.tool.findmyphone.data.repo

import android.content.Context
import com.mtg.tool.findmyphone.ADS_SOUND_TYPE
import com.mtg.tool.findmyphone.DEFAULT_SOUND_TYPE
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.data.db.RoomDatabase
import com.mtg.tool.findmyphone.data.model.SoundItem
import java.io.File

object AppRepository {
    private var listAllSoundImport = mutableListOf<SoundItem>()
    fun getAllSound(context: Context): List<SoundItem> {
        return arrayListOf(
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.cat_meowing),
                0,
                R.drawable.image_cat_meowing,
                R.drawable.avatar_cat_meowing,
                "file:///android_asset/cat_meowing.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.dog_barking),
                0,
                R.drawable.image_dog_barking,
                R.drawable.avatar_dog_barking,
                "file:///android_asset/dog_barking.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.hey_stay_here),
                0,
                R.drawable.image_i_am_here,
                R.drawable.avatar_i_am_here,
                "file:///android_asset/i_am_here.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.whistle),
                0,
                R.drawable.image_whistle,
                R.drawable.avatar_whistle,
                "file:///android_asset/whistle.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.hello),
                0,
                R.drawable.image_hello,
                R.drawable.avatar_hello,
                "file:///android_asset/hello.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.car_horn),
                0,
                R.drawable.image_car_honk,
                R.drawable.avatar_car_honk,
                "file:///android_asset/car_honk.mp3"
            ),
            SoundItem(ADS_SOUND_TYPE),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.door_bell),
                0,
                R.drawable.image_door_bell,
                R.drawable.avatar_door_bell,
                "file:///android_asset/door_bell.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.party_horn),
                0,
                R.drawable.image_party_horn,
                R.drawable.avatar_party_horn,
                "file:///android_asset/party_horn.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.police_whistle),
                0,
                R.drawable.image_police_whistle,
                R.drawable.avatar_police_whistle,
                "file:///android_asset/police_whistle.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.trumpet),
                0,
                R.drawable.image_trumpet,
                R.drawable.avatar_trumpet,
                "file:///android_asset/trumpet.mp3"
            )
        )
    }

    fun getAllSoundImport(): MutableList<SoundItem> {
        if (listAllSoundImport.isEmpty()) {
            RoomDatabase.getDatabase()?.soundDao()?.getAllSound()
                ?.let { listAllSoundImport.addAll(it) }
        }
        return listAllSoundImport
    }

    fun checkHasSound(name: String): Boolean{
        for (soundItem in listAllSoundImport) {
            if (soundItem.name == name) {
                return true
            }
        }
        return false
    }

    fun insertSound(soundItem: SoundItem) {
        listAllSoundImport.add(soundItem)
        RoomDatabase.getDatabase()?.soundDao()?.insert(soundItem)
    }

    fun updateName(path: String, newName: String) {
        val soundItem = listAllSoundImport.find { soundItem -> soundItem.soundPath == path }
        soundItem?.name = newName
        RoomDatabase.getDatabase()?.soundDao()?.updateName(path, newName)
    }
    fun deleteSound(path: String) {
        val soundItem = listAllSoundImport.find { soundItem -> soundItem.soundPath == path }
        File(path).delete()
        listAllSoundImport.remove(soundItem)
        RoomDatabase.getDatabase()?.soundDao()?.delete(path)
    }
}