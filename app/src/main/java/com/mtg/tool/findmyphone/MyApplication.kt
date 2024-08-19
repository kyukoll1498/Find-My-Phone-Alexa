package com.mtg.tool.findmyphone

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.common.control.MyApplication
import com.common.control.dialog.PermissionStorageDialog
import com.common.control.manager.AppOpenManager
import com.common.control.model.PurchaseModel
import com.facebook.FacebookSdk
import com.mbridge.msdk.MBridgeConstans
import com.mbridge.msdk.MBridgeSDK
import com.mbridge.msdk.out.MBridgeSDKFactory
import com.mtg.tool.findmyphone.ads_executor.inter.InterSplashExecutor
import com.mtg.tool.findmyphone.consent_dialog.remote_config.RemoteConfigManager
import com.mtg.tool.findmyphone.data.db.RoomDatabase
import com.mtg.tool.findmyphone.main.activity.InterestActivity
import com.mtg.tool.findmyphone.main.activity.Language2Activity
import com.mtg.tool.findmyphone.main.activity.LanguageActivity
import com.mtg.tool.findmyphone.main.activity.MainActivity
import com.mtg.tool.findmyphone.main.activity.OnBoardActivity
import com.mtg.tool.findmyphone.main.activity.PermissionActivity
import com.mtg.tool.findmyphone.main.activity.SplashActivity
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import com.vungle.ads.VunglePrivacySettings.setCCPAStatus
import com.vungle.ads.VunglePrivacySettings.setGDPRStatus


class MyApplication : MyApplication(), Application.ActivityLifecycleCallbacks {
    companion object{
        const val PRODUCT_SUBS = "subscription.clap.fullaccess"
        const val PRODUCT_LIFETIME = "com.clap.buyforever"
    }
    private val lsActivity = ArrayList<Activity>()



    override fun onApplicationCreate() {
        val sdk: MBridgeSDK = MBridgeSDKFactory.getMBridgeSDK()
        sdk.setConsentStatus(applicationContext, MBridgeConstans.IS_SWITCH_ON)

        setGDPRStatus(true, "v2.2.0")
        setCCPAStatus(true)


        RoomDatabase.initDatabase(this)
        AppPreferences(this)
        RemoteConfigManager.instance?.loadRemote()

        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionStorageDialog::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(LanguageActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(InterestActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(OnBoardActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(Language2Activity::class.java)
//        AppOpenManager.getInstance().disableAppResumeWithRewardActivity(HomeActivity::class.java)
//        AppOpenManager.getInstance().specialAppResumeWithActivity(IncomingActivity::class.java)
        registerActivityLifecycleCallbacks(this)
        EventLogger.init(applicationContext)
        //todo facebook sdk
//        AudienceNetworkInitializeHelper.initialize(this)
//        FacebookSdk.sdkInitialize(this)

//        Common.printHashKey(this)

//        InterSplashExecutor.loadInterAds(applicationContext)
    }

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        super.onActivityPreCreated(activity, savedInstanceState)
        val removableActivities = ArrayList<Activity>()
        if (activity is SplashActivity) {
            if (!lsActivity.isEmpty()) {
                for (a in lsActivity) {
                    a.finish()
                    removableActivities.add(a)
                }
            }
        }
        lsActivity.removeAll(removableActivities)
        lsActivity.add(activity)
    }

    override fun hasAdjust(): Boolean {
        return false
    }

    override fun getAdjustAppToken(): String? {
        return null
    }

    override fun hasAds(): Boolean {
        return true
    }

    override fun isShowDialogLoadingAd(): Boolean {
        return true
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
        return MainActivity::class.java
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