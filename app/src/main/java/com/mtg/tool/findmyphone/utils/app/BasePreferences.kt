package com.mtg.tool.findmyphone.utils.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

open class BasePreferences
@SuppressLint("CommitPrefEdits")
constructor(context: Context, shareName: String) {

    companion object {

        private var instance: BasePreferences? = null

        fun getInstance(context: Context, shareName: String): BasePreferences {
            if (instance == null) {
                instance =
                    BasePreferences(
                        context,
                        shareName
                    )
            }
            return instance as BasePreferences
        }
    }

    private var appSharedPrefs: SharedPreferences? = null

    var prefsEditor: SharedPreferences.Editor? = null

    init {
        try {
            appSharedPrefs = context.getSharedPreferences(
                shareName,
                Context.MODE_PRIVATE
            )
            prefsEditor = appSharedPrefs!!.edit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Int Type
     */
    fun putInt(key: String, value: Int) {
        try {
            prefsEditor?.run {
                putInt(key, value)
                commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return try {
            appSharedPrefs?.run { getInt(key, defaultValue) } ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }


    /**
     * Long Type
     */
    fun putLong(key: String, value: Long): BasePreferences {
        try {
            prefsEditor?.run {
                putLong(key, value)
                commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    fun getLong(key: String, defaultValue: Long = 0): Long {
        return appSharedPrefs?.getLong(key, defaultValue) ?: defaultValue
    }


    /**
     * Boolean TYPE
     */
    fun putBoolean(key: String, value: Boolean): BasePreferences {
        try {
            prefsEditor?.run {
                putBoolean(key, value)
                commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    @JvmOverloads
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return appSharedPrefs?.getBoolean(key, defaultValue) ?: defaultValue
    }


    /**
     * String type
     */
    fun putString(key: String, value: String) {
        try {
            prefsEditor?.run {
                putString(key, value)
                commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmOverloads
    fun getString(key: String, defaultValue: String = ""): String? {
        return appSharedPrefs?.getString(key, defaultValue)
    }

    /**
     * Object Type
     */

    inline fun <reified T : Any> putObject(key: String, ob: T): BasePreferences {
        try {
            val json: String = Gson().toJson(ob)
            prefsEditor?.run {
                putString(key, json)
                commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    inline fun <reified T : Any> getObject(key: String): T {
        val string = getString(key)
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson(string, type)
    }

    /**
     * Remove a SharedPreference
     */
    fun remove(key: String) {
        prefsEditor?.remove(key)?.apply()
    }


    /**
     * Clear all cache in SharedPreference
     */
    fun clearCache(): BasePreferences {
        prefsEditor?.clear()?.commit()
        return this
    }
}
