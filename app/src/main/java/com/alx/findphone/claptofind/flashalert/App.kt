package com.alx.findphone.claptofind.flashalert

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.alx.findphone.claptofind.flashalert.consent_dialog.remote_config.RemoteConfigManager
import com.alx.findphone.claptofind.flashalert.data.db.RoomSoundDB
import com.alx.findphone.claptofind.flashalert.main.activity.SplashActivity
import com.alx.findphone.claptofind.flashalert.main.fragment.HomeFragment
import com.alx.findphone.claptofind.flashalert.utils.EventLogger
import com.alx.findphone.claptofind.flashalert.utils.app.AppPreferences
import com.common.control.MyApplication
import com.common.control.model.PurchaseModel
import com.facebook.FacebookSdk
import com.facebook.ads.AudienceNetworkAds
import com.facebook.appevents.AppEventsLogger
import com.mbridge.msdk.MBridgeConstans
import com.mbridge.msdk.MBridgeSDK
import com.mbridge.msdk.out.MBridgeSDKFactory
import com.reyun.solar.engine.SolarEngineConfig
import com.reyun.solar.engine.SolarEngineManager
import com.vungle.ads.VunglePrivacySettings.setCCPAStatus
import com.vungle.ads.VunglePrivacySettings.setGDPRStatus


class App : MyApplication(), Application.ActivityLifecycleCallbacks {
    companion object {
        const val PRODUCT_SUBS = "subscription.clap.fullaccess"
        const val PRODUCT_LIFETIME = "com.clap.buyforever"
        const val APP_KEY_SOLAR_ENGINE = "7de59d5c8d57e2f5"
    }

    val TAG = "FindMyPhone"
    private val lsActivity = ArrayList<Activity>()


    override fun onApplicationCreate() {
        val sdk: MBridgeSDK = MBridgeSDKFactory.getMBridgeSDK()
        sdk.setConsentStatus(applicationContext, MBridgeConstans.IS_SWITCH_ON)

        setGDPRStatus(true, "v2.2.0")
        setCCPAStatus(true)

        initSolarEngine()

        RoomSoundDB.Companion.initDatabase(this)
        AppPreferences(this)
        RemoteConfigManager.Companion.instance?.loadRemote()

        registerActivityLifecycleCallbacks(this)
        EventLogger.Companion.init(applicationContext)

        //Facebook SDK
        FacebookSdk.sdkInitialize(this)
        AudienceNetworkAds.initialize(applicationContext);
        AudienceNetworkInitializeHelper.initialize(this)

        AppEventsLogger.activateApp(this);

//        Common.printHashKey(this)

//        InterSplashExecutor.loadInterAds(applicationContext)
    }

    private fun initSolarEngine() {
        SolarEngineManager.getInstance().preInit(this, APP_KEY_SOLAR_ENGINE)
        val config = SolarEngineConfig.Builder()
            .setFbAppID(getString(R.string.facebook_app_id))
            .build()
        config.isDebugModel = false
        SolarEngineManager.getInstance().initialize(
            this, APP_KEY_SOLAR_ENGINE, config
        ) { code: Int ->
            if (code == 0) {
                //Init success
                Log.i(TAG, "initSolarEngineSuccess: $code")
            } else {
                //Init failed
                Log.i(TAG, "initSolarEngineSuccess: $code")
            }
        }
    }

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityPreCreated(activity, savedInstanceState)
        val removableActivities = ArrayList<Activity>()
        if (activity is SplashActivity) {
            if (lsActivity.isNotEmpty()) {
                for (a in lsActivity) {
                    a.finish()
                    removableActivities.add(a)
                }
            }
        }
        lsActivity.removeAll(removableActivities.toSet())
        lsActivity.add(activity)
    }

    override fun hasAdjust(): Boolean {
        return false
    }

    override fun getAdjustAppToken(): String? {
        return null
    }

    override fun hasAds(): Boolean {
        return false
    }

    override fun isShowDialogLoadingAd(): Boolean {
        return false
    }

    override fun isShowAdsTest(): Boolean {
        return BuildConfig.TEST_AD || BuildConfig.DEBUG
    }

    override fun enableAdsResume(): Boolean {
        return true
    }

    override fun getOpenAppAdId(): String {
        return BuildConfig.app_reopen
    }

    override fun getPolicyUrl(): String {
        return ""
    }

    override fun getSubjectSupport(): String {
        return ""
    }

    override fun getEmailSupport(): String {
        return ""
    }


    override fun isInitBilling(): Boolean {
        return true
    }

    override fun getPurchaseList(): List<PurchaseModel?>? {
        return listOf<PurchaseModel>(PurchaseModel(PRODUCT_SUBS, PurchaseModel.ProductType.SUBS))
    }

    override fun getPurchaseListInApp(): List<PurchaseModel?>? {
        return listOf<PurchaseModel>(PurchaseModel(PRODUCT_LIFETIME, PurchaseModel.ProductType.INAPP))
    }

    override fun getFirstActForOpenApp(): Class<*> {
        return HomeFragment::class.java
    }


    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
    }

    override fun onActivityPaused(p0: Activity) {
    }

    override fun onActivityStopped(p0: Activity) {
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }


}