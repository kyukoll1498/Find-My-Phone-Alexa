package com.alx.findphone.claptofind.flashalert.utils.app

import android.content.Context
import com.alx.findphone.claptofind.flashalert.HIGH_SENSITIVITY
import com.alx.findphone.claptofind.flashalert.KEY_CHOOSE_LANGUAGE
import com.alx.findphone.claptofind.flashalert.KEY_CURRENT_DURATION
import com.alx.findphone.claptofind.flashalert.KEY_CURRENT_IDX_LANGUAGE
import com.alx.findphone.claptofind.flashalert.KEY_CURRENT_LANGUAGE
import com.alx.findphone.claptofind.flashalert.KEY_CURRENT_VOLUME
import com.alx.findphone.claptofind.flashalert.KEY_HAS_FLASH
import com.alx.findphone.claptofind.flashalert.KEY_HAS_SOUND
import com.alx.findphone.claptofind.flashalert.KEY_HAS_VIBRATE
import com.alx.findphone.claptofind.flashalert.KEY_SOUND_APPLY
import com.alx.findphone.claptofind.flashalert.MODE_FLASH_DEFAULT
import com.alx.findphone.claptofind.flashalert.MODE_VIBRATE_DEFAULT
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.data.model.ItemAlert
import com.alx.findphone.claptofind.flashalert.data.model.SoundItem
import com.alx.findphone.claptofind.flashalert.data.repo.AppRepository
import com.alx.findphone.claptofind.flashalert.utils.AudioMangerUtils
import com.alx.findphone.claptofind.flashalert.utils.LanguageUtils
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants.KEY_ITEM_ALERT
import com.google.gson.Gson

class AppPreferences(val context: Context, val mGson: Gson = Gson()) : BasePreferences(context, context.packageName) {
    init {
        instance = this
    }

    companion object {
        lateinit var instance: AppPreferences
        const val KEY_VIBRATE = "KEY_VIBRATE"
        const val KEY_FLASH = "KEY_FLASH"
        const val KEY_SOUND_SENSITIVITY = "KEY_SOUND_SENSITIVITY"
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

    inline var currentSoundSensitivity: Int
        get() {
            return getInt(KEY_SOUND_SENSITIVITY, HIGH_SENSITIVITY)
        }
        set(value) {
            putInt(KEY_SOUND_SENSITIVITY, value)
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

    fun setCurrentItemAlert(value: ItemAlert) {
        putString(Constants.KEY_ITEM_ALERT, mGson.toJson(value))
    }

    fun getCurrentItemAlert(): ItemAlert {
        val currentItem = getString(KEY_ITEM_ALERT, "")
        if (currentItem.isEmpty()) {
            return ItemAlert(R.drawable.sc_alert1, R.drawable.sc_alert1, R.drawable.bt_turnoff1, R.color.black, false)
        }
        return mGson.fromJson(currentItem, ItemAlert::class.java)
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
            return getInt(
                KEY_CURRENT_IDX_LANGUAGE, LanguageUtils.listCountryDefault.indexOf(
                    LanguageUtils.getRemoteConfigListCountry()[1]
                )
            )
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