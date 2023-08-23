package com.mtg.tool.findmyphone.utils

import android.content.Context
import android.content.res.Resources
import android.text.TextUtils
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.data.model.ItemLanguage
import com.mtg.tool.findmyphone.data.preferences.SharedPrefs
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import com.mtg.tool.findmyphone.utils.constant.Constants
import java.util.Locale

object LanguageUtils {
    private var appPreferences = AppPreferences.instance
    val listCountry: List<ItemLanguage>
        get() {
            val mList: MutableList<ItemLanguage> = ArrayList()
            mList.add(ItemLanguage(R.drawable.flag_en, "English", R.drawable.ic_disable, "en"))
            mList.add(ItemLanguage(R.drawable.flag_ar, "Arabic", R.drawable.ic_disable, "ar"))
            mList.add(ItemLanguage(R.drawable.flag_bg, "Bulgarian", R.drawable.ic_disable, "bg"))
            mList.add(
                ItemLanguage(
                    R.drawable.flag_zh,
                    "Chinese (Simplified)",
                    R.drawable.ic_disable,
                    "zh"
                )
            )
            mList.add(ItemLanguage(R.drawable.flag_cs, "Czech", R.drawable.ic_disable, "cs"))
            mList.add(ItemLanguage(R.drawable.flag_pl, "Polish", R.drawable.ic_disable, "pl"))
            mList.add(ItemLanguage(R.drawable.flag_nl, "Dutch", R.drawable.ic_disable, "ru"))
            mList.add(
                ItemLanguage(
                    R.drawable.flag_fr,
                    "French (France)",
                    R.drawable.ic_disable,
                    "fr"
                )
            )
            mList.add(ItemLanguage(R.drawable.flag_de, "German", R.drawable.ic_disable, "de"))
            mList.add(ItemLanguage(R.drawable.flag_el, "Greek", R.drawable.ic_disable, "el"))
            mList.add(ItemLanguage(R.drawable.flag_hi, "Hindi", R.drawable.ic_disable, "hi"))
            mList.add(ItemLanguage(R.drawable.flag_it, "Italian", R.drawable.ic_disable, "it"))
            mList.add(ItemLanguage(R.drawable.flag_in, "Indonesian", R.drawable.ic_disable, "in"))
            mList.add(ItemLanguage(R.drawable.flag_ko, "Korean", R.drawable.ic_disable, "ko"))
            mList.add(ItemLanguage(R.drawable.flag_ru, "Russian", R.drawable.ic_disable, "ru"))
            mList.add(ItemLanguage(R.drawable.flag_ro, "Romanian", R.drawable.ic_disable, "ro"))
            mList.add(ItemLanguage(R.drawable.flag_sv, "Swedish", R.drawable.ic_disable, "sv"))
            mList.add(
                ItemLanguage(
                    R.drawable.flag_es,
                    "Spanish (Spain)",
                    R.drawable.ic_disable,
                    "es"
                )
            )
            mList.add(ItemLanguage(R.drawable.flag_th, "Thai", R.drawable.ic_disable, "th"))
            mList.add(
                ItemLanguage(
                    R.drawable.flag_pt,
                    "Portuguese (Portugal)",
                    R.drawable.ic_disable,
                    "pt"
                )
            )
            mList.add(ItemLanguage(R.drawable.flag_vi, "Vietnamese", R.drawable.ic_disable, "vi"))

            return mList
        }

    fun getFlagResourceID(context: Context): Int {
        val itemLanguage = listCountry.findLast { it.languageToLoad.equals(appPreferences.currentLanguage, true) } ?: return R.drawable.flag_en
        return itemLanguage.imageFlag
    }


    fun getCurrentLanguageCode(context: Context): String {
        val languageName = SharedPrefs.getString(context, Constants.SHARE_PREF_LANGUAGE, "default")
        val itemLanguage = listCountry.findLast { it.languageToLoad.equals(languageName, true) } ?: return "en"
        return itemLanguage.languageToLoad
    }

    open fun getDefaultLanguage(): String{
        val userLang = Resources.getSystem().configuration.locales.get(0).language
        if (!TextUtils.isEmpty(userLang) && checkLanguageAvailable(userLang)) {
            return userLang
        } else {
            return Locale.ENGLISH.language
        }
    }

    private fun checkLanguageAvailable(userLang: String): Boolean {
        for (language in listCountry) {
            if (language.languageToLoad == userLang) {
                return true
            }
        }
        return false
    }

    fun getDefaultItemLanguage(): ItemLanguage? {
        return listCountry.find { itemLanguage -> itemLanguage.languageToLoad == getDefaultLanguage() }
    }
}