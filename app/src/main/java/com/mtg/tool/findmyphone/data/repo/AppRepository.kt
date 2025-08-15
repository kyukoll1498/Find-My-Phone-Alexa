package com.mtg.tool.findmyphone.data.repo

import android.content.Context
import com.mtg.tool.findmyphone.ADS_SOUND_TYPE
import com.mtg.tool.findmyphone.DEFAULT_SOUND_TYPE
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.data.db.RoomSoundDB
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
                R.drawable.avt_dog,
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
                R.drawable.avt_police_1,
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

    fun getSoundMain(context: Context): List<SoundItem> {
        val listItem = arrayListOf(
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.police_1),
                0,
                R.drawable.avt_police_1,
                R.drawable.avt_police_1,
                "file:///android_asset/police_1.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.alarm_1),
                0,
                R.drawable.avt_alarm_1,
                R.drawable.avt_alarm_1,
                "file:///android_asset/alarm_1.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.dog),
                0,
                R.drawable.avt_dog,
                R.drawable.avt_dog,
                "file:///android_asset/dog.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.laughing),
                0,
                R.drawable.avt_laughing,
                R.drawable.avt_laughing,
                "file:///android_asset/whistle.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.ghost_1),
                0,
                R.drawable.avt_ghost_1,
                R.drawable.avt_ghost_1,
                "file:///android_asset/ghost_1.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.lion),
                0,
                R.drawable.avt_lion,
                R.drawable.avt_lion,
                "file:///android_asset/lion.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.air_horn),
                0,
                R.drawable.avt_air_horn,
                R.drawable.avt_air_horn,
                "file:///android_asset/air_horn.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.devil),
                0,
                R.drawable.avt_devil,
                R.drawable.avt_devil,
                "file:///android_asset/devil.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.fire_alarm),
                0,
                R.drawable.avt_fire_alarm,
                R.drawable.avt_fire_alarm,
                "file:///android_asset/fire_alarm.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.grenade),
                0,
                R.drawable.avt_grenade,
                R.drawable.avt_grenade,
                "file:///android_asset/grenade.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.gun),
                0,
                R.drawable.avt_gun,
                R.drawable.avt_gun,
                "file:///android_asset/gun.mp3"
            ),
            SoundItem(
                DEFAULT_SOUND_TYPE,
                context.getString(R.string.ambulance),
                0,
                R.drawable.avt_ambulance,
                R.drawable.avt_ambulance,
                "file:///android_asset/ambulance.mp3"
            )
        )

        return listItem
    }

    fun getAllSoundImport(): MutableList<SoundItem> {
        if (listAllSoundImport.isEmpty()) {
            RoomSoundDB.getDatabase()?.soundDao()?.getAllSound()
                ?.let { listAllSoundImport.addAll(it) }
        }
        return listAllSoundImport
    }

    fun checkHasSound(name: String): Boolean {
        for (soundItem in listAllSoundImport) {
            if (soundItem.name == name) {
                return true
            }
        }
        return false
    }

    fun insertSound(soundItem: SoundItem) {
        listAllSoundImport.add(soundItem)
        RoomSoundDB.getDatabase()?.soundDao()?.insert(soundItem)
    }

    fun updateName(path: String, newName: String) {
        val soundItem = listAllSoundImport.find { soundItem -> soundItem.soundPath == path }
        soundItem?.name = newName
        RoomSoundDB.getDatabase()?.soundDao()?.updateName(path, newName)
    }

    fun deleteSound(path: String) {
        val soundItem = listAllSoundImport.find { soundItem -> soundItem.soundPath == path }
        File(path).delete()
        listAllSoundImport.remove(soundItem)
        RoomSoundDB.getDatabase()?.soundDao()?.delete(path)
    }
}