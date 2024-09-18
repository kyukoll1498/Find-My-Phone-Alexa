package com.mtg.tool.findmyphone.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.control.base.OnActionCallback
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.utils.InternetUtil
import com.google.android.gms.ads.nativead.NativeAd
import com.mtg.tool.findmyphone.AdCache
import com.mtg.tool.findmyphone.AppSession
import com.mtg.tool.findmyphone.AdIds
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.consent_dialog.remote_config.RemoteConfigManager
import com.mtg.tool.findmyphone.data.model.ItemLanguage
import com.mtg.tool.findmyphone.data.preferences.SharedPrefs
import com.mtg.tool.findmyphone.databinding.ActivityLanguageBinding
import com.mtg.tool.findmyphone.main.adapter.LanguageAdapter
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.LanguageUtils
import com.mtg.tool.findmyphone.utils.LanguageUtils.listCountry
import com.mtg.tool.findmyphone.utils.LanguageUtils.listCountryDefault
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import com.mtg.tool.findmyphone.utils.constant.Constants

class LFO2Activity : BaseActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {
    private var mList: List<ItemLanguage> = ArrayList()
    private var languageAdapter: LanguageAdapter? = null
    private var itemLanguage: ItemLanguage? = null
    private var appPreferences = AppPreferences.instance
    private val lfo1NativeAds = arrayListOf(AdIds.lfo1_native_high, AdIds.lfo1_native)
    private val lfo2NativeAds = arrayListOf(AdIds.lfo2_native_high2, AdIds.lfo2_native)
    private val lfo2NativeAdsReload = arrayListOf(AdIds.lfo2_native_high, AdIds.lfo2_native)
    private val ob1NativeAds = arrayListOf(AdIds.ob1_native_high, AdIds.ob1_native)
    private var isFirstResume = true

    override fun initView() {
        logEvent("LFO2_view")
        if (AdCache.getInstance().lfo2NativeHigh != null) {
            AdmobManager.getInstance().showNative(
                this@LFO2Activity,
                AdCache.getInstance().lfo2NativeHigh,
                binding.frAd2,
                AdmobManager.NativeAdType.BIG
            )
        } else if (AdCache.getInstance().lfo2NativeHigh1 != null) {
            AdmobManager.getInstance().showNative(
                this@LFO2Activity,
                AdCache.getInstance().lfo2NativeHigh1,
                binding.frAd2,
                AdmobManager.NativeAdType.BIG
            )
        } else if (AdCache.getInstance().lfo2NativeHigh2 != null) {
            AdmobManager.getInstance().showNative(
                this@LFO2Activity,
                AdCache.getInstance().lfo2NativeHigh2,
                binding.frAd2,
                AdmobManager.NativeAdType.BIG
            )
        }
        binding.frAd.visibility = View.GONE
        AdmobManager.getInstance().preloadNative(
            this@LFO2Activity,
            AdIds.ob4_native_high,
            object : AdCallback() {
                override fun onNativeAds(nativeAd: NativeAd?) {
                    super.onNativeAds(nativeAd)
                    AdCache.getInstance().ob4NativeHigh = nativeAd
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    logEvent("onboard4_native_view")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    logEvent("onboard4_native_click")

                }
            }

        )
        initRemoteConfig()
        RemoteConfigManager.instance!!.fetchAndActivate {
            setStatusBarColor()
            initListLanguage()
            initRCLanguage()
            handleButtonBack()
        }
    }

    private fun initRemoteConfig() {
        if (InternetUtil.isNetworkAvailable(this)) {
            RemoteConfigManager.instance!!.fetchAndActivate {
                setStatusBarColor()
                initListLanguage()
                initRCLanguage()
                handleButtonBack()
            }
        } else {
            setStatusBarColor()
            initListLanguage()
            initRCLanguage()
            handleButtonBack()
        }
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
        (binding.rcLanguage.layoutManager as LinearLayoutManager).onRestoreInstanceState(
            intent.getParcelableExtra(
                "state"
            )
        )
    }

    private fun initListLanguage() {
        mList = LanguageUtils.getRemoteConfigListCountry()
        itemLanguage = listCountryDefault[intent.getIntExtra("pos", 1)]
        itemLanguage?.colorBackground = "#ED6A40"
        itemLanguage?.imgSelect = (R.drawable.ic_checked)
    }

    override fun addEvent() {
        binding.btBack.setOnClickListener { finish() }
        binding.ivDone.setOnClickListener {
            logEvent("complete_lfo_flow")
            EventLogger.getInstance()?.logEvent("click_language_tick")
            //Intent intent = new Intent(this, MainActivity.class);
            if (itemLanguage == null) {
                itemLanguage = LanguageUtils.getDefaultItemLanguage()
            }
            appPreferences.currentLanguage = itemLanguage!!.languageToLoad
            appPreferences.currentIndexLanguage = listCountryDefault.indexOf(itemLanguage)
            appPreferences.isChooseLanguage = true

            setLanguageWithoutNotification(itemLanguage!!.languageToLoad)

            if (!SharedPrefs.getBoolean(this, Constants.SKIP_ONBOARD)) {
                InterestActivity.start(this)
                finish()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (isFirstResume) {
            isFirstResume = false
        } else {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, lfo2NativeAdsReload, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdmobManager.getInstance().showNative(
                            this@LFO2Activity,
                            nativeAd,
                            binding.frAd2,
                            AdmobManager.NativeAdType.BIG
                        )
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        logEvent("language2_ad_native_view")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        logEvent("language2_ad_native_click")

                    }
                })
        }
    }

}