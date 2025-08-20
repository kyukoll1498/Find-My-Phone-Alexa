package com.alx.findphone.claptofind.flashalert.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.alx.findphone.claptofind.flashalert.AdCache
import com.alx.findphone.claptofind.flashalert.AdIds
import com.alx.findphone.claptofind.flashalert.AppSession
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.base.BaseAdapter
import com.alx.findphone.claptofind.flashalert.data.model.ItemLanguage
import com.alx.findphone.claptofind.flashalert.data.preferences.SharedPrefs
import com.alx.findphone.claptofind.flashalert.databinding.ActivityLanguageBinding
import com.alx.findphone.claptofind.flashalert.main.adapter.LanguageAdapter
import com.alx.findphone.claptofind.flashalert.main.fragment.HomeFragment
import com.alx.findphone.claptofind.flashalert.utils.LanguageUtils
import com.alx.findphone.claptofind.flashalert.utils.LanguageUtils.listCountryDefault
import com.alx.findphone.claptofind.flashalert.utils.app.AppPreferences
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.common.control.base.OnActionCallback
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.utils.InternetUtil
import com.google.android.gms.ads.nativead.NativeAd

class LanguageActivity : BaseActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {
    private var mList: List<ItemLanguage> = ArrayList()
    private var languageAdapter: LanguageAdapter? = null
    private var itemLanguage: ItemLanguage? = null
    private var appPreferences = AppPreferences.Companion.instance
    private val lfo1NativeAds = arrayListOf(AdIds.lfo1_native_high, AdIds.lfo1_native)
    private val lfo2NativeAds = arrayListOf(AdIds.lfo2_native_high2, AdIds.lfo2_native)
    private val ob1NativeAds = arrayListOf(AdIds.ob1_native_high, AdIds.ob1_native)

    override fun initView() {
        logEvent("LFO1_view")
        AdCache.getInstance().lfo1Native.observe(
            this
        ) { value ->
            AdmobManager.getInstance().showNative(
                this@LanguageActivity,
                value,
                binding.frAd,
                AdmobManager.NativeAdType.BIG
            )
        }
        if (InternetUtil.isNetworkAvailable(this)) {
            if (AdCache.getInstance().lfo2NativeHigh == null && AdCache.getInstance().lfo2NativeHigh1 == null) {
                AdmobManager.getInstance()
                    .preloadAlternateNative(this, lfo2NativeAds, object : AdCallback() {
                        override fun onNativeAds(nativeAd: NativeAd?) {
                            super.onNativeAds(nativeAd)
                            AdCache.getInstance().lfo2NativeHigh2 = nativeAd
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
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, ob1NativeAds, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdCache.getInstance().ob1Native = nativeAd
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        logEvent("onboard1_native_view")
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        logEvent("onboard1_native_click")

                    }
                })
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
                logEvent("language_choose_" + itemLanguage?.name + "_click")
                logEvent("complete_lfo1")
                startActivity(Intent(this@LanguageActivity, LFO2Activity::class.java).apply {
                    putExtra("pos", listCountryDefault.indexOf(itemLanguage))
                    putExtra("state", binding.rcLanguage.layoutManager!!.onSaveInstanceState())
                })
                finish()
                overridePendingTransition(0, 0)
            }
        }
        binding.rcLanguage.layoutManager = LinearLayoutManager(this)
        binding.rcLanguage.adapter = languageAdapter
    }

    private fun initListLanguage() {
        mList = LanguageUtils.getRemoteConfigListCountry()
//        itemLanguage = listCountryDefault[appPreferences.currentIndexLanguage]
//        itemLanguage?.colorBackground = "#ED6A40"
//        itemLanguage?.imgSelect = (R.drawable.ic_checked)
    }

    override fun addEvent() {
        binding.btBack.setOnClickListener { finish() }
        binding.ivDone.setOnClickListener {
            logEvent("complete_lfo")
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

    override fun onResume() {
        super.onResume()
        if (AppSession.isCompletedInterSplash) {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, lfo1NativeAds, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdmobManager.getInstance().showNative(
                            this@LanguageActivity,
                            nativeAd,
                            binding.frAd,
                            AdmobManager.NativeAdType.BIG
                        )
                    }

                    override fun onAdClicked() {
                        super.onAdClicked()
                        logEvent("language_ad_native_click")
                    }

                    override fun onAdImpression() {
                        super.onAdImpression()
                        logEvent("language_ad_native_view")

                    }
                })
        }
    }

}