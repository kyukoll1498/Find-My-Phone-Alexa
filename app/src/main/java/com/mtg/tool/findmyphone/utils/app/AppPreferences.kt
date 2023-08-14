package com.mtg.tool.findmyphone.utils.app

import android.content.Context
import com.mtg.tool.findmyphone.MODE_FLASH_DEFAULT
import com.mtg.tool.findmyphone.MODE_FLASH_DISCO
import com.mtg.tool.findmyphone.MODE_VIBRATE_DEFAULT
import com.mtg.tool.findmyphone.MODE_VIBRATE_STRONG

class AppPreferences (val context: Context) :
    BasePreferences(context, context.packageName){
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


}