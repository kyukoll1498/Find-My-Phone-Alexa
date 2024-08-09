package com.mtg.tool.findmyphone.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.mtg.tool.findmyphone.AdCache
import com.mtg.tool.findmyphone.AppSession
import com.mtg.tool.findmyphone.AdIds
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.consent_dialog.ConsentDialogManager
import com.mtg.tool.findmyphone.databinding.ActivitySplashBinding
import com.mtg.tool.findmyphone.utils.Common
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.app.AppPreferences

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private var appPreferences = AppPreferences.instance

    private val bannerAds by lazy {
        arrayListOf(AdIds.banner_splash_high, AdIds.banner_splash)
    }
    private val interAds by lazy { arrayListOf(AdIds.inter_splash_high, AdIds.inter_splash) }
    private val lfo1NativeAds by lazy { arrayListOf(AdIds.lfo1_native_high, AdIds.lfo1_native) }
    private val lfo2NativeHighAds by lazy { AdIds.lfo2_native_high }
    private val lfo2NativeHigh1Ads by lazy { AdIds.lfo2_native_high1 }

    private var canNextScreen = MutableLiveData<Boolean>(false)

    override fun binding() {
        isFullScreen = true
        super.binding()
    }

    override fun initView() {
        Handler().postDelayed(Runnable {
            canNextScreen.value = true
        }, 3000)
        AppSession.isCompletedInterSplash = false
        logEvent("view_splash")
        ConsentDialogManager.instance?.showConsentDialogSplash(this) {
            AdIds.updateIdAdsWithRemoteConfig()
            handleAds()
        }
        setImageBackground()
//        Handler(Looper.getMainLooper()).postDelayed({ handleAds() }, 2000)
        EventLogger.getInstance()?.logEvent("open_splash")

    }

    private fun setImageBackground() {
        if (Common.screenWidth / Common.screenHeight > 108 / 216) {
            binding.imgBackground.layoutParams.height = Common.screenHeight
        } else {
            binding.imgBackground.layoutParams.width = Common.screenWidth
        }
    }

    private fun handleAds() {
        try {
            if (!appPreferences.isChooseLanguage) {
//            1. preload lfo2_native_high
                preloadNativeLanguage2()
//            2 - 3. load alternate inter_splash_high, inter_splash
                loadAlternateInter()
//            4 - 5. load alternate banner_splash_high, banner_splash
                loadAlternateBanner()
            } else {
//            1 - 2. load alternate banner_splash_high, banner_splash
                loadAlternateBanner()
//            3 - 4. load alternate inter_splash_high, inter_splash
                loadAlternateInter()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadAlternateBanner() {
        AdmobManager.getInstance().loadAlternateBanner(this, bannerAds, binding.frAd)
    }

    private fun loadAlternateInter() {
        AdmobManager.getInstance()
            .loadAlternateInter(this, interAds, object : AdCallback() {
                override fun onResultInterstitialAd(interstitialAd: InterstitialAd?) {
                    super.onResultInterstitialAd(interstitialAd)
                    canNextScreen.observe(
                        this@SplashActivity
                    ) {
                        if (it) {
                            showAdAndStartMain(interstitialAd)
                        }
                    }
                }

                override fun onAdFailedToLoad(i: LoadAdError) {
                    super.onAdFailedToLoad(i)
                    canNextScreen.observe(
                        this@SplashActivity
                    ) {
                        if (it) {
                            startMain()
                            Handler().postDelayed(Runnable {
                                AppSession.isCompletedInterSplash = true
                            }, 300)
                        }
                    }
                }
            })
    }

    private fun showAdAndStartMain(interstitialAd: InterstitialAd?) {
        AdmobManager.getInstance().showInterstitial(
            this@SplashActivity,
            interstitialAd,
            object : AdCallback() {
                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    logEvent("view_inter_splash")
                    if (AdCache.getInstance().lfo2NativeHigh == null) {
//                                      1. preload lfo2_native_high1
                        if (lfo2NativeHigh1Ads.isNotBlank()) {
                            AdmobManager.getInstance().preloadNative(
                                this@SplashActivity,
                                lfo2NativeHigh1Ads,
                                object : AdCallback() {
                                    override fun onNativeAds(nativeAd: NativeAd?) {
                                        super.onNativeAds(nativeAd)
                                        AdCache.getInstance().lfo2NativeHigh1 =
                                            nativeAd
                                    }
                                })
                        }
                    }
//                                    2. preload alternate lfo1_native_high, lfo1_native
                    AdmobManager.getInstance().preloadAlternateNative(
                        this@SplashActivity,
                        lfo1NativeAds,
                        object : AdCallback() {
                            override fun onNativeAds(nativeAd: NativeAd?) {
                                super.onNativeAds(nativeAd)
                                AdCache.getInstance().lfo1Native.value = nativeAd
                            }
                        })
                }

                override fun onNextScreen() {
                    super.onNextScreen()
                    startMain()
                    Handler().postDelayed(Runnable {
                        AppSession.isCompletedInterSplash = true
                    }, 300)

                }
            })

    }

    private fun preloadNativeLanguage2() {
        if (lfo2NativeHighAds.isNotBlank()) {
            AdmobManager.getInstance()
                .preloadNative(this, AdIds.lfo2_native_high, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdCache.getInstance().lfo2NativeHigh = nativeAd
                    }
                })
        }
    }


    private fun startMain() {
        if (!appPreferences.isChooseLanguage) {
            val intent = Intent(this, LanguageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        } else {
            val intent = Intent(this, PermissionActivity::class.java)
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

    }
}