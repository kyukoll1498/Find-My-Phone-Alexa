package com.alx.findphone.claptofind.flashalert.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alx.findphone.claptofind.flashalert.AdCache
import com.alx.findphone.claptofind.flashalert.AdIds
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.base.BaseAdapter
import com.alx.findphone.claptofind.flashalert.data.model.ItemLanguage
import com.alx.findphone.claptofind.flashalert.data.preferences.SharedPrefs
import com.alx.findphone.claptofind.flashalert.databinding.ActivityLanguageBinding
import com.alx.findphone.claptofind.flashalert.main.adapter.LanguageAdapter
import com.alx.findphone.claptofind.flashalert.main.fragment.HomeFragment
import com.alx.findphone.claptofind.flashalert.utils.EventLogger
import com.alx.findphone.claptofind.flashalert.utils.LanguageUtils
import com.alx.findphone.claptofind.flashalert.utils.app.AppPreferences
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.alx.findphone.claptofind.flashalert.utils.show
import com.common.control.base.OnActionCallback
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.utils.InternetUtil
import com.google.android.gms.ads.nativead.NativeAd

class LFO2Activity : BaseActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {
    private var mList: List<ItemLanguage> = ArrayList()
    private var languageAdapter: LanguageAdapter? = null
    private var itemLanguage: ItemLanguage? = null
    private var appPreferences = AppPreferences.Companion.instance
    private val lfo2NativeAdsReload = arrayListOf(AdIds.lfo2_native_high, AdIds.lfo2_native)
    private var isFirstResume = true

    override fun initView() {
        binding.ivDone.show()
        logEvent("LFO2_view")
        if (AdCache.getInstance().lfo2NativeHigh != null) {
            AdmobManager.getInstance().showNative(
                this@LFO2Activity, AdCache.getInstance().lfo2NativeHigh, binding.frAd2, AdmobManager.NativeAdType.BIG
            )
        } else if (AdCache.getInstance().lfo2NativeHigh1 != null) {
            AdmobManager.getInstance().showNative(
                this@LFO2Activity, AdCache.getInstance().lfo2NativeHigh1, binding.frAd2, AdmobManager.NativeAdType.BIG
            )
        } else if (AdCache.getInstance().lfo2NativeHigh2 != null) {
            AdmobManager.getInstance().showNative(
                this@LFO2Activity, AdCache.getInstance().lfo2NativeHigh2, binding.frAd2, AdmobManager.NativeAdType.BIG
            )
        }
        binding.frAd.visibility = View.GONE
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance().preloadNative(
                this@LFO2Activity, AdIds.ob4_native_high, object : AdCallback() {
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
        }
        initRemoteConfig()
    }

    private fun initRemoteConfig() {
        setStatusBarColor()
        initListLanguage()
        initRCLanguage()
        handleButtonBack()
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
        itemLanguage = LanguageUtils.listCountryDefault[intent.getIntExtra("pos", 1)]
        itemLanguage?.colorBackground = "#ED6A40"
        itemLanguage?.imgSelect = (R.drawable.ic_checked)
    }

    override fun addEvent() {
        binding.btBack.setOnClickListener { finish() }
        binding.ivDone.setOnClickListener {
            logEvent("complete_lfo_flow")
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
            AdmobManager.getInstance().preloadAlternateNative(this, lfo2NativeAdsReload, object : AdCallback() {
                override fun onNativeAds(nativeAd: NativeAd?) {
                    super.onNativeAds(nativeAd)
                    AdmobManager.getInstance().showNative(
                        this@LFO2Activity, nativeAd, binding.frAd2, AdmobManager.NativeAdType.BIG
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