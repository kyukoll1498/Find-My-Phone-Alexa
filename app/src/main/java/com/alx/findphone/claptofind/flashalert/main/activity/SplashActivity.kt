package com.alx.findphone.claptofind.flashalert.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.alx.findphone.claptofind.flashalert.AdCache
import com.alx.findphone.claptofind.flashalert.AdIds
import com.alx.findphone.claptofind.flashalert.AppSession
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.consent_dialog.remote_config.RemoteConfigManager
import com.alx.findphone.claptofind.flashalert.data.preferences.SharedPrefs
import com.alx.findphone.claptofind.flashalert.databinding.ActivitySplashBinding
import com.alx.findphone.claptofind.flashalert.main.fragment.HomeFragment
import com.alx.findphone.claptofind.flashalert.utils.Common
import com.alx.findphone.claptofind.flashalert.utils.EventLogger
import com.alx.findphone.claptofind.flashalert.utils.app.AppPreferences
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.bumptech.glide.Glide
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.utils.InternetUtil
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import java.util.Timer
import java.util.TimerTask

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private var appPreferences = AppPreferences.Companion.instance

    private val bannerAds by lazy {
        arrayListOf(AdIds.banner_splash_high, AdIds.banner_splash)
    }
    private val interAds by lazy { arrayListOf(AdIds.inter_splash_high, AdIds.inter_splash) }
    private val lfo1NativeAds by lazy { arrayListOf(AdIds.lfo1_native_high, AdIds.lfo1_native) }
    private val lfo2NativeHighAds by lazy { AdIds.lfo2_native_high }
    private val lfo2NativeHigh1Ads by lazy { AdIds.lfo2_native_high1 }
    private val onb1NativeAds by lazy { arrayListOf(AdIds.ob1_native_high, AdIds.ob1_native) }
    private var canRefreshBanner = true
    private var isShowedInter = false

    private val adCallbackBanner = object : AdCallback() {
        override fun onAdImpression() {
            super.onAdImpression()
            logEvent("splash_ad_banner_view")
        }

        override fun onAdClicked() {
            super.onAdClicked()
            logEvent("splash_ad_banner_click")
        }
    }


    private var canNextScreen = MutableLiveData<Boolean>(false)

    override fun binding() {
        isFullScreen = true
        super.binding()
    }

    override fun initView() {
        Glide.with(this).load(R.drawable.img_bg_spash).into(binding.imgBackground)
//        Handler().postDelayed(Runnable {
//            canNextScreen.value = true
//        }, 10000)
        AppSession.isCompletedInterSplash = false
        logEvent("splash_view")
//        ConsentDialogManager.instance?.showConsentDialogSplash(this) {
//            AdIds.updateIdAdsWithRemoteConfig()
//            handleAds()
//        }
        handleAds()
        setImageBackground()
//        Handler(Looper.getMainLooper()).postDelayed({ handleAds() }, 2000)
        EventLogger.Companion.getInstance()?.logEvent("open_splash")

    }

    private fun setImageBackground() {
        if (Common.screenWidth / Common.screenHeight > 108 / 216) {
            binding.imgBackground.layoutParams.height = Common.screenHeight
        } else {
            binding.imgBackground.layoutParams.width = Common.screenWidth
        }
    }

    private fun handleAds() {
//        try {
//            if (!appPreferences.isChooseLanguage) {
////            1. preload lfo2_native_high
//                preloadNativeLanguage2()
////            2 - 3. load alternate inter_splash_high, inter_splash
//                loadAlternateInter()
////            4 - 5. load alternate banner_splash_high, banner_splash
//                loadAlternateBanner()
//            } else {
//                if (SharedPrefs.getBoolean(this, Constants.SKIP_ONBOARD)) {
////            1 - 2. load alternate banner_splash_high, banner_splash
//                    loadAlternateBanner()
////            3 - 4. load alternate inter_splash_high, inter_splash
//                    loadAlternateInter()
//                } else {
////            1 - 2. preload onb1_native_high, onb1_native
//                    preloadNativeOb1()
////            3 - 4. load alternate inter_splash_high, inter_splash
//                    loadAlternateInter()
////            5 - 6. load alternate banner_splash_high, banner_splash
//                    loadAlternateBanner()
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        Handler(mainLooper).postDelayed({ startMain() }, 4000)
    }

    private fun loadAlternateBanner() {
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance()
                .loadAlternateBanner(this, bannerAds, binding.frAd, adCallbackBanner)

            val timeLoad = RemoteConfigManager.Companion.instance!!.time_load_banner
            if (timeLoad != 0L) {
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        runOnUiThread {
                            Log.d("devLogger: canRefreshBanner", canRefreshBanner.toString())
                            Log.d("devLogger: isShowedInter", isShowedInter.toString())
                            if (canRefreshBanner && !isShowedInter) {
                                reloadBanner()
                            }
                        }
                    }
                }, timeLoad * 1000, timeLoad * 1000)
            }
        } else {
            Log.d("devLogger: loadAlternateBanner", "No network available, skipping banner load.")
        }
    }

    private fun reloadBanner() {
        if (bannerAds.isNotEmpty() && InternetUtil.isNetworkAvailable(this)) {
            Log.d("AdmobRefresh: ", "splash" + bannerAds[0])
            AdmobManager.getInstance()
                .loadAlternateBanner(
                    this,
                    arrayListOf(bannerAds[0]),
                    binding.frAd,
                    adCallbackBanner
                )
        }
    }

    private fun loadAlternateInter() {
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance()
                .loadAlternateInter(this, interAds, object : AdCallback() {
                    override fun onResultInterstitialAd(interstitialAd: InterstitialAd?) {
                        super.onResultInterstitialAd(interstitialAd)
                        canNextScreen.observe(this@SplashActivity) {
                            if (it) {
                                showAdAndStartMain(interstitialAd)
                            }
                        }
                    }

                    override fun onAdFailedToLoad(i: LoadAdError) {
                        super.onAdFailedToLoad(i)
                        canNextScreen.observe(this@SplashActivity) {
                            if (it) {
                                startMain()
                                Handler().postDelayed({
                                    AppSession.isCompletedInterSplash = true
                                }, 300)
                            }
                        }
                    }
                })
        } else {
            Log.d(
                "devLogger: loadAlternateInter",
                "No network available, skipping interstitial load."
            )
            canNextScreen.observe(this@SplashActivity) {
                if (it) {
                    startMain()
                    Handler().postDelayed({
                        AppSession.isCompletedInterSplash = true
                    }, 300)
                }
            }
        }
    }

    private fun showAdAndStartMain(interstitialAd: InterstitialAd?) {
        isShowedInter = true
        AdmobManager.getInstance().showInterstitial(
            this@SplashActivity,
            interstitialAd,
            object : AdCallback() {
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    logEvent("splash_ad_inter_view")
                    if (!appPreferences.isChooseLanguage) {
                        if (AdCache.getInstance().lfo2NativeHigh == null) {
//                                      1. preload lfo2_native_high1
                            if (lfo2NativeHigh1Ads.isNotBlank()) {
                                if (InternetUtil.isNetworkAvailable(mContext)) {
                                    AdmobManager.getInstance().preloadNative(
                                        this@SplashActivity,
                                        lfo2NativeHigh1Ads,
                                        object : AdCallback() {
                                            override fun onNativeAds(nativeAd: NativeAd?) {
                                                super.onNativeAds(nativeAd)
                                                AdCache.getInstance().lfo2NativeHigh1 =
                                                    nativeAd
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
//                                    2. preload alternate lfo1_native_high, lfo1_native
                        if (InternetUtil.isNetworkAvailable(mContext)) {
                            AdmobManager.getInstance().preloadAlternateNative(
                                this@SplashActivity,
                                lfo1NativeAds,
                                object : AdCallback() {
                                    override fun onNativeAds(nativeAd: NativeAd?) {
                                        super.onNativeAds(nativeAd)
                                        AdCache.getInstance().lfo1Native.value = nativeAd
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
                    } else if (!SharedPrefs.getBoolean(
                            this@SplashActivity,
                            Constants.SKIP_ONBOARD
                        )
                    ) {
                        preloadNativeOb4()
                    }
                }

                override fun onNextScreen() {
                    super.onNextScreen()
                    startMain()
                    Handler().postDelayed(Runnable {
                        AppSession.isCompletedInterSplash = true
                    }, 300)

                }

                override fun onClickClose() {
                    super.onClickClose()
                    logEvent("splash_ad_inter_close_click")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    logEvent("splash_ad_inter_click")
                }
            })

    }

    private fun preloadNativeLanguage2() {
        if (InternetUtil.isNetworkAvailable(this) && lfo2NativeHighAds.isNotBlank()) {
            AdmobManager.getInstance()
                .preloadNative(this, AdIds.lfo2_native_high, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdCache.getInstance().lfo2NativeHigh = nativeAd
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

    private fun preloadNativeOb1() {
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance()
                .preloadAlternateNative(this, onb1NativeAds, object : AdCallback() {
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
    }

    private fun preloadNativeOb4() {
        if (InternetUtil.isNetworkAvailable(this)) {
            AdmobManager.getInstance()
                .preloadFullScreenNative(this, AdIds.ob4_native_high, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdCache.getInstance().ob4NativeHigh = nativeAd
                    }
                })
        }
    }


    private fun startMain() {
        if (shouldShowShortcut()) {
            val intent = HomeFragment.newIntent(this)
            if (this.intent.data != null) {
                intent.putExtra(Constants.key_shortcut, this.intent.data?.toString())
            }
            startActivity(intent)
            finish()
            return
        }

        if (!appPreferences.isChooseLanguage) {
            val intent = Intent(this, LanguageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else if (!SharedPrefs.getBoolean(this, Constants.SKIP_ONBOARD)) {
            val intent = Intent(this, OnBoardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            val intent = Intent(this, HomeFragment::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    override fun addEvent() {

    }

    override fun onResume() {
        super.onResume()
        canRefreshBanner = true
    }

    override fun onPause() {
        super.onPause()
        canRefreshBanner = false
    }

    private fun shouldShowShortcut(): Boolean {
        val shortcut = intent.data?.toString()
        return shortcut in arrayOf(
            Constants.shortcut_uninstall,
        )
    }
}