package com.mtg.tool.findmyphone

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.common.control.AppConfig
import com.common.control.MyApplication
import com.common.control.dialog.PermissionStorageDialog
import com.common.control.manager.AppOpenManager
import com.common.control.model.PurchaseModel
import com.facebook.FacebookSdk
import com.mtg.tool.findmyphone.data.db.RoomDatabase
import com.mtg.tool.findmyphone.main.activity.SplashActivity
import com.mtg.tool.findmyphone.utils.Common
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import java.util.*


class MyApplication : MyApplication(), Application.ActivityLifecycleCallbacks {

    val PRODUCT_LIFETIME_OLD = "com"
    val PRODUCT_LIFETIME = "com"
    private val lsActivity = ArrayList<Activity>()



    override fun onApplicationCreate() {
        RoomDatabase.initDatabase(this)
        AppPreferences(this)


        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionStorageDialog::class.java)
//        AppOpenManager.getInstance().disableAppResumeWithRewardActivity(HomeActivity::class.java)
//        AppOpenManager.getInstance().specialAppResumeWithActivity(IncomingActivity::class.java)
        registerActivityLifecycleCallbacks(this)
        EventLogger.init(applicationContext)
        //todo facebook sdk
        AudienceNetworkInitializeHelper.initialize(this)
        FacebookSdk.sdkInitialize(this)

//        Common.printHashKey(this)
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
        return BuildConfig.TEST_AD
    }

    override fun enableAdsResume(): Boolean {
        return true
    }

    override fun getOpenAppAdId(): String {
        return BuildConfig.open_app
    }

    override fun isInitBilling(): Boolean {
        return false
    }

    override fun getPurchaseList(): MutableList<PurchaseModel> {
        return Arrays.asList<PurchaseModel>(
            PurchaseModel(PRODUCT_LIFETIME_OLD, PurchaseModel.ProductType.INAPP),
            PurchaseModel(PRODUCT_LIFETIME, PurchaseModel.ProductType.INAPP)
        )
    }

    override fun getAppConfig(): AppConfig {
        return AppConfig.AppConfigBuilder().setShowLogIdAd(true).build()
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