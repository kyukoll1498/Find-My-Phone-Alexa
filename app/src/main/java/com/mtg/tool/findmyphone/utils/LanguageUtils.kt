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
            listCountryDefault.forEach {
                it.imgSelect = R.drawable.ic_disable
                it.colorBackground = null
            }
            val mList: MutableList<ItemLanguage> = ArrayList()
            mList.addAll(listCountryDefault)
            val languageCodeDefault = Resources.getSystem().configuration.locales.get(0).language
            if (mList[0].languageToLoad == languageCodeDefault || mList[1].languageToLoad == languageCodeDefault) {
                return mList
            } else {
                val item = mList.findLast { it.languageToLoad == languageCodeDefault }
                if (item != null) {
                    mList.remove(item)
                    mList.add(2, item)
                }
                return mList
            }
        }

    val listCountryDefault: List<ItemLanguage> by lazy {
        val mList: MutableList<ItemLanguage> = ArrayList()
        mList.add(
            ItemLanguage(
                R.drawable.flag_fr,
                "Français",
                R.drawable.ic_disable,
                "fr"
            )
        )
        mList.add(ItemLanguage(R.drawable.flag_en, "English", R.drawable.ic_disable, "en"))
        mList.add(ItemLanguage(R.drawable.flag_hi, "हिंदी", R.drawable.ic_disable, "hi"))
        mList.add(ItemLanguage(R.drawable.flag_pk, "پاکستان", R.drawable.ic_disable, "ur"))
        mList.add(ItemLanguage(R.drawable.flag_br, "Brasil", R.drawable.ic_disable, "pt"))
        mList.add(ItemLanguage(R.drawable.flag_es, "España", R.drawable.ic_disable, "es"))
        mList.add(ItemLanguage(R.drawable.flag_ru, "Pусский", R.drawable.ic_disable, "ru"))
        mList.add(ItemLanguage(R.drawable.flag_hi, "ভারত", R.drawable.ic_disable, "bn"))
        mList.add(ItemLanguage(R.drawable.flag_mxc, "México", R.drawable.ic_disable, "es"))
        return@lazy mList
    }


    fun getFlagResourceID(context: Context): Int {
        val itemLanguage =
            listCountry.findLast { it.languageToLoad.equals(appPreferences.currentLanguage, true) }
                ?: return R.drawable.flag_en
        return itemLanguage.imageFlag
    }


    fun getCurrentLanguageCode(context: Context): String {
        val languageName = SharedPrefs.getString(context, Constants.SHARE_PREF_LANGUAGE, "default")
        val itemLanguage =
            listCountry.findLast { it.languageToLoad.equals(languageName, true) } ?: return "en"
        return itemLanguage.languageToLoad
    }

    open fun getDefaultLanguage(): String {
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