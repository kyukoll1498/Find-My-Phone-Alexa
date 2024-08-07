package com.mtg.tool.findmyphone.main.activity

import android.content.Intent
import android.os.Handler
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.mtg.tool.findmyphone.AdCache
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.consent_dialog.ConsentDialogManager
import com.mtg.tool.findmyphone.databinding.ActivitySplashBinding
import com.mtg.tool.findmyphone.utils.Common
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.app.AppPreferences

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private var appPreferences = AppPreferences.instance

    val bannerAds = arrayListOf(BuildConfig.banner_splash_high, BuildConfig.banner_splash)
    val interAds = arrayListOf(BuildConfig.inter_splash_high, BuildConfig.inter_splash)
    override fun binding() {
        isFullScreen = true
        super.binding()
    }

    override fun initView() {
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
            AdmobManager.getInstance().loadAlternateBanner(this, bannerAds, binding.frAd)
            AdmobManager.getInstance()
                .preloadNative(this, BuildConfig.lfo2_native_high, object : AdCallback() {
                    override fun onNativeAds(nativeAd: NativeAd?) {
                        super.onNativeAds(nativeAd)
                        AdCache.getInstance().lfo2NativeHigh = nativeAd
                    }
                })
            AdmobManager.getInstance().loadAlternateInter(this, interAds, object : AdCallback() {
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
                                    if (AdCache.getInstance().lfo2NativeHigh == null) {
                                        AdmobManager.getInstance().preloadNative(
                                            this@SplashActivity,
                                            BuildConfig.lfo2_native_high,
                                            object : AdCallback() {
                                                override fun onNativeAds(nativeAd: NativeAd?) {
                                                    super.onNativeAds(nativeAd)
                                                    AdCache.getInstance().lfo2NativeHigh = nativeAd
                                                }
                                            })
                                    }
                                }
                            })
                    }, 1000)

                }

                override fun onAdFailedToLoad(i: LoadAdError) {
                    super.onAdFailedToLoad(i)
                    Handler().postDelayed(Runnable {
                        startMain()
                    }, 1000)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
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