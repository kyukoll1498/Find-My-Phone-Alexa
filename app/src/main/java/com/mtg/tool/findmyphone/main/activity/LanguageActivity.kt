package com.mtg.tool.findmyphone.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.akexorcist.localizationactivity.core.LanguageSetting.setLanguage
import com.common.control.base.OnActionCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.model.ItemLanguage
import com.mtg.tool.findmyphone.data.preferences.SharedPrefs
import com.mtg.tool.findmyphone.databinding.ActivityLanguageBinding
import com.mtg.tool.findmyphone.main.adapter.LanguageAdapter
import com.mtg.tool.findmyphone.utils.Common
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.LanguageUtils
import com.mtg.tool.findmyphone.utils.LanguageUtils.listCountry
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import com.mtg.tool.findmyphone.utils.constant.Constants

class LanguageActivity : BaseActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {
    private var mList: List<ItemLanguage> = ArrayList()
    private var languageAdapter: LanguageAdapter? = null
    private var itemLanguage: ItemLanguage? = null
    private var appPreferences = AppPreferences.instance

    override fun initView() {
        setStatusBarColor()
        initListLanguage()
        initRCLanguage()
        handleButtonBack()

        if (Common.screenWidth / Common.screenHeight > 108 / 216) {
            binding.imgBackground.layoutParams.height = Common.screenHeight
        } else {
            binding.imgBackground.layoutParams.width = Common.screenWidth
        }
    }

    override fun loadAds() {
        super.loadAds()
        AdmobManager.getInstance().loadNative(this, BuildConfig.native_language, binding.frAd, R.layout.custom_native_language)
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(this, binding.frAd)
    }

    private fun setStatusBarColor() {
        window.navigationBarColor = ContextCompat.getColor(this, R.color.color_1D1C21)
    }

    private fun handleButtonBack() {
        if (!appPreferences.isChooseLanguage) {
            binding.btBack.visibility = View.GONE
        } else {
            binding.btBack.visibility = View.VISIBLE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRCLanguage() {
        languageAdapter = LanguageAdapter(mList, this).apply {
            mCallback = OnActionCallback { key, data ->
                for (item in mList) {
                    item?.let { it.imgSelect = (R.drawable.ic_disable) }
                }
                if (key == Constants.KEY_LANGUAGE) {
                    itemLanguage = data[0] as ItemLanguage?
                    itemLanguage?.let { it.imgSelect = (R.drawable.ic_checked) }
                    this.notifyDataSetChanged()
                }
            }
        }
        binding.rcLanguage.layoutManager = LinearLayoutManager(this)
        binding.rcLanguage.adapter = languageAdapter
    }

    private fun initListLanguage() {
        mList = listCountry
        for (i in mList.indices) {
            if (mList[i].languageToLoad == appPreferences.currentLanguage) {
                itemLanguage = mList[i]
                mList[i].imgSelect = (R.drawable.ic_checked)
                return
            }
        }
    }

    override fun addEvent() {
        binding.btBack.setOnClickListener { finish() }
        binding.ivDone.setOnClickListener {
            EventLogger.getInstance()?.logEvent("click_language_tick")
            //Intent intent = new Intent(this, MainActivity.class);
            if (itemLanguage == null) {
                itemLanguage = LanguageUtils.getDefaultItemLanguage()
            }
            appPreferences.currentLanguage = itemLanguage!!.languageToLoad
            appPreferences.isChooseLanguage = true
            setLanguage(itemLanguage!!.languageToLoad)
            //todo go to next
            if (SharedPrefs.getBoolean(this, "is_skip_onboard")) {
                finish()
            } else {
                startActivity(Intent(this, OnBoardActivity::class.java))
                finish()
            }
        }
    }

}