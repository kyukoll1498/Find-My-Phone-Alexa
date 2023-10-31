package com.mtg.tool.findmyphone.main.activity

import android.content.Intent
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.example.voicelockscreen.ads_executor.inter.InterSplashExecutor
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.consent_dialog.ConsentDialogManager
import com.mtg.tool.findmyphone.databinding.ActivitySplashBinding
import com.mtg.tool.findmyphone.utils.Common
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.app.AppPreferences

class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private var appPreferences = AppPreferences.instance
    override fun binding() {
        isFullScreen = true
        super.binding()
    }

    override fun initView() {
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
            AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
            InterSplashExecutor.loadInterAds(this)
            InterSplashExecutor.showInterAds(this, object : InterSplashExecutor.OnBeforeShowCallback{
                override fun callback() {
                    startMain()
                }
            }){}
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun startMain() {
        if (!appPreferences.isChooseLanguage) {
            startActivity(Intent(this, LanguageActivity::class.java))
        } else {
            //todo
//            if (SharedPrefs.getBoolean(this, "is_skip_onboard")) {
//                startActivity(Intent(this, MainActivity::class.java))
//                if (!PermissionUtils.checkMicroPermission(mContext)){
                    startActivity(Intent(this@SplashActivity, PermissionActivity::class.java))
//                }
//            } else {
//                startActivity(Intent(this@SplashActivity, OnBoardActivity::class.java))
//            }
        }
//        Handler(Looper.getMainLooper()).postDelayed({
//            finish()
//        }, 300)
    }

    override fun onStart() {
        super.onStart()
//        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this@SplashActivity, OnBoardActivity::class.java)) //check onboard first time in OnboardActivity
//        }, 2000)
    }

    override fun addEvent() {

    }

    override fun onResume() {
        super.onResume()
//        if (AdCache.getInstance().interGuide == null) {
//            AdmobManager.getInstance()
//                .loadInterAds(this, BuildConfig.inter_guide, object : AdCallback() {
//                    override fun onResultInterstitialAd(interstitialAd: InterstitialAd?) {
//                        super.onResultInterstitialAd(interstitialAd)
//                        AdCache.getInstance().interGuide = interstitialAd
//                    }
//                })
//        }
    }
}