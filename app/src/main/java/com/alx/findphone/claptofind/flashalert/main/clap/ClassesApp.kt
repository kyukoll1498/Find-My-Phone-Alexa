package com.alx.findphone.claptofind.flashalert.main.clap

import android.content.Context
import android.preference.PreferenceManager

internal class ClassesApp(private val mContext: Context) {
    fun read(str: String?, str2: String?): String? {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getString(str, str2)
    }

    fun save(str: String?, str2: String?) {
        val edit = PreferenceManager.getDefaultSharedPreferences(mContext).edit()
        edit.putString(str, str2)
        edit.apply()
    }
}