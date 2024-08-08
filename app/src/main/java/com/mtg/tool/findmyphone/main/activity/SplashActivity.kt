package com.mtg.tool.findmyphone.main.activity

import android.content.Intent
import android.os.Handler
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.mtg.tool.findmyphone.AdCache
import com.mtg.tool.findmyphone.AppSession
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.consent_dialog.ConsentDialogManager
import com.mtg.tool.findmyphone.consent_dialog.remote_config.RemoteConfigManager
import com.mtg.tool.findmyphone.databinding.ActivitySplashBinding
import com.mtg.tool.findmyphone.utils.Common
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.app.AppPreferences

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private var appPreferences = AppPreferences.instance

    val bannerAds = arrayListOf(BuildConfig.banner_splash_high, BuildConfig.banner_splash)
    val interAds = arrayListOf(BuildConfig.inter_splash_high, BuildConfig.inter_splash)
    val lfo1NativeAds = arrayListOf(BuildConfig.lfo1_native_high, BuildConfig.lfo1_native)
    var lfo2NativeHighAds = BuildConfig.lfo2_native_high
    var lfo2NativeHigh1Ads = BuildConfig.lfo2_native_high1
    override fun binding() {
        isFullScreen = true
        super.binding()
    }

    override fun initView() {
        AppSession.isCompletedInterSplash = false
        logEvent("view_splash")
        ConsentDialogManager.instance?.showConsentDialogSplash(this) {
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
                updateRemoteConfig()
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

    private fun updateRemoteConfig() {
        if (!RemoteConfigManager.instance!!._101_splash_n_banner_high) {
            bannerAds.removeIf { it == BuildConfig.banner_splash_high }
        }
        if (!RemoteConfigManager.instance!!._101_splash_n_banner) {
            bannerAds.removeIf { it == BuildConfig.banner_splash }
        }
        if (!RemoteConfigManager.instance!!._102_splash_n_inter_high) {
            interAds.removeIf { it == BuildConfig.inter_splash_high }
        }
        if (!RemoteConfigManager.instance!!._102_splash_n_inter) {
            interAds.removeIf { it == BuildConfig.inter_splash }
        }
        if (!RemoteConfigManager.instance!!._202_lfo_n_native_high) {
            lfo2NativeHighAds = ""
        }
        if (!RemoteConfigManager.instance!!._202_lfo_n_native_high_1) {
            lfo2NativeHigh1Ads = ""
        }
        if (!RemoteConfigManager.instance!!._201_lfo_n_native_high) {
            lfo1NativeAds.removeIf { it == BuildConfig.lfo1_native_high }
        }
        if (!RemoteConfigManager.instance!!._201_lfo_n_native) {
            lfo1NativeAds.removeIf { it == BuildConfig.lfo1_native }
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
                    Handler().postDelayed(Runnable {
                        startMain()
                        AdmobManager.getInstance().showInterstitial(
                            this@SplashActivity,
                            interstitialAd,
                            object : AdCallback() {
                                override fun onAdShowedFullScreenContent() {
                                    super.onAdShowedFullScreenContent()
                                    logEvent("view_inter_splash")
                                    if (AdCache.getInstance().lfo2NativeHigh == null) {
//                                      1. preload lfo2_native_high1
                                        if (lfo2NativeHigh1Ads.isNotBlank()){
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
                                    Handler().postDelayed(Runnable {
                                        AppSession.isCompletedInterSplash = false
                                    }, 300)

                                }
                            })
                    }, 1000)

                }

                override fun onAdFailedToLoad(i: LoadAdError) {
                    super.onAdFailedToLoad(i)
                    Handler().postDelayed(Runnable {
                        startMain()
                        Handler().postDelayed(Runnable {
                            AppSession.isCompletedInterSplash = false
                        }, 300)
                    }, 1000)
                }
            })
    }

    private fun preloadNativeLanguage2() {
        if (lfo2NativeHighAds.isNotBlank()) {
            AdmobManager.getInstance()
                .preloadNative(this, BuildConfig.lfo2_native_high, object : AdCallback() {
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