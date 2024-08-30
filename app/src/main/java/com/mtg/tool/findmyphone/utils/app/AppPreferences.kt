package com.mtg.tool.findmyphone.utils.app

import android.content.Context
import com.google.gson.Gson
import com.mtg.tool.findmyphone.KEY_CHOOSE_LANGUAGE
import com.mtg.tool.findmyphone.KEY_CURRENT_DURATION
import com.mtg.tool.findmyphone.KEY_CURRENT_IDX_LANGUAGE
import com.mtg.tool.findmyphone.KEY_CURRENT_LANGUAGE
import com.mtg.tool.findmyphone.KEY_CURRENT_VOLUME
import com.mtg.tool.findmyphone.KEY_HAS_FLASH
import com.mtg.tool.findmyphone.KEY_HAS_SOUND
import com.mtg.tool.findmyphone.KEY_HAS_VIBRATE
import com.mtg.tool.findmyphone.KEY_SOUND_APPLY
import com.mtg.tool.findmyphone.MODE_FLASH_DEFAULT
import com.mtg.tool.findmyphone.MODE_VIBRATE_DEFAULT
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.data.repo.AppRepository
import com.mtg.tool.findmyphone.utils.AudioMangerUtils
import com.mtg.tool.findmyphone.utils.LanguageUtils

class AppPreferences(val context: Context, val mGson: Gson = Gson()) :
    BasePreferences(context, context.packageName) {
    init {
        instance = this
    }

    companion object {
        lateinit var instance: AppPreferences
        const val KEY_VIBRATE = "KEY_VIBRATE"
        const val KEY_FLASH = "KEY_FLASH"
    }

    inline var currentVibrate: Int
        get() {
            return getInt(KEY_VIBRATE, MODE_VIBRATE_DEFAULT)
        }
        set(value) {
            putInt(KEY_VIBRATE, value)
        }

    inline var currentFlash: Int
        get() {
            return getInt(KEY_FLASH, MODE_FLASH_DEFAULT)
        }
        set(value) {
            putInt(KEY_FLASH, value)
        }

    inline var hasSound: Boolean
        get() {
            return getBoolean(KEY_HAS_SOUND, true)
        }
        set(value) {
            putBoolean(KEY_HAS_SOUND, value)
        }
    inline var hasFlash: Boolean
        get() {
            return getBoolean(KEY_HAS_FLASH, true)
        }
        set(value) {
            putBoolean(KEY_HAS_FLASH, value)
        }
    inline var hasVibrate: Boolean
        get() {
            return getBoolean(KEY_HAS_VIBRATE, true)
        }
        set(value) {
            putBoolean(KEY_HAS_VIBRATE, value)
        }

    inline var currentSound: SoundItem
        get() {
            var currentSound = getString(KEY_SOUND_APPLY)
            if (currentSound == "") {
                currentSound = mGson.toJson(AppRepository.getAllSound(context)[0])
            }
            return mGson.fromJson(currentSound, SoundItem::class.java)
        }
        set(value) {
            putString(KEY_SOUND_APPLY, mGson.toJson(value))
        }
    inline var currentDuration: Int
        get() {
            return getInt(KEY_CURRENT_DURATION, 30000)
        }
        set(value) {
            putInt(KEY_CURRENT_DURATION, value)
        }
    inline var currentVolume: Int
        get() {
            return getInt(KEY_CURRENT_VOLUME, AudioMangerUtils.getDefaultVolume(context))
        }
        set(value) {
            putInt(KEY_CURRENT_VOLUME, value)
        }
    inline var currentLanguage: String
        get() {
            return getString(KEY_CURRENT_LANGUAGE, "en")
        }
        set(value) {
            putString(KEY_CURRENT_LANGUAGE, value)
        }
    inline var currentIndexLanguage: Int
        get() {
            return getInt(KEY_CURRENT_IDX_LANGUAGE, LanguageUtils.listCountryDefault.indexOf(
                LanguageUtils.getRemoteConfigListCountry()[1]
            ))
        }
        set(value) {
            putInt(KEY_CURRENT_IDX_LANGUAGE, value)
        }
    inline var isChooseLanguage: Boolean
        get() {
//            return false
            return getBoolean(KEY_CHOOSE_LANGUAGE, false)
        }
        set(value) {
            putBoolean(KEY_CHOOSE_LANGUAGE, true)
        }
}