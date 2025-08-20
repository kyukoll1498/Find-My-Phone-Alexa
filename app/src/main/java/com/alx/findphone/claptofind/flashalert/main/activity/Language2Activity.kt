package com.alx.findphone.claptofind.flashalert.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alx.findphone.claptofind.flashalert.BuildConfig
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.data.model.ItemLanguage
import com.alx.findphone.claptofind.flashalert.data.preferences.SharedPrefs
import com.alx.findphone.claptofind.flashalert.databinding.ActivityLanguageBinding
import com.alx.findphone.claptofind.flashalert.main.adapter.LanguageAdapter
import com.alx.findphone.claptofind.flashalert.main.fragment.HomeFragment
import com.alx.findphone.claptofind.flashalert.utils.EventLogger
import com.alx.findphone.claptofind.flashalert.utils.LanguageUtils
import com.alx.findphone.claptofind.flashalert.utils.app.AppPreferences
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.common.control.base.OnActionCallback
import com.common.control.manager.AdmobManager

class Language2Activity : BaseActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {
    private var mList: List<ItemLanguage> = ArrayList()
    private var languageAdapter: LanguageAdapter? = null
    private var itemLanguage: ItemLanguage? = null
    private var appPreferences = AppPreferences.Companion.instance

    override fun initView() {
        logEvent("language2_setting_view")
        setStatusBarColor()
        initRemoteConfig()
    }

    private fun initRemoteConfig() {
        initListLanguage()
        initRCLanguage()
        handleButtonBack()
        binding.ivDone.setImageResource(R.drawable.ic_tick_done)
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
                    itemLanguage?.colorBackground = null
                }
                if (key == Constants.KEY_LANGUAGE) {
                    itemLanguage = data[0] as ItemLanguage?
                    itemLanguage?.let { it.imgSelect = (R.drawable.ic_checked) }
                    itemLanguage?.colorBackground = "#ED6A40"
                    this.notifyDataSetChanged()
                }
            }
        }
        binding.rcLanguage.layoutManager = LinearLayoutManager(this)
        binding.rcLanguage.adapter = languageAdapter
    }

    private fun initListLanguage() {
        mList = LanguageUtils.getRemoteConfigListCountry()
        itemLanguage = LanguageUtils.listCountryDefault[appPreferences.currentIndexLanguage]
        itemLanguage?.colorBackground = "#ED6A40"
        itemLanguage?.imgSelect = (R.drawable.ic_checked)
    }

    override fun addEvent() {
        binding.btBack.setOnClickListener { finish() }
        binding.ivDone.setOnClickListener {
            EventLogger.Companion.getInstance()?.logEvent("click_language_tick")
            //Intent intent = new Intent(this, MainActivity.class);
            if (itemLanguage == null) {
                itemLanguage = LanguageUtils.getDefaultItemLanguage()
            }
            appPreferences.currentLanguage = itemLanguage!!.languageToLoad
            appPreferences.currentIndexLanguage = LanguageUtils.listCountryDefault.indexOf(itemLanguage)
            appPreferences.isChooseLanguage = true

            setLanguageWithoutNotification(itemLanguage!!.languageToLoad)

            if (!SharedPrefs.getBoolean(this, Constants.SKIP_ONBOARD)) {
//                InterestActivity.start(this)
                OnBoardActivity.start(this)
                finish()
            } else {
                val intent = Intent(this, HomeFragment::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadNative()
    }

    private fun loadNative() {
        AdmobManager.getInstance().loadNative(this, BuildConfig.native_language_setting, binding.frAd2, AdmobManager.NativeAdType.BIG)
    }
}