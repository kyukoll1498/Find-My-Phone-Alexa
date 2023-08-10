package com.common.control;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.LogLevel;
import com.common.control.manager.AdmobManager;
import com.common.control.manager.AppOpenManager;
import com.common.control.manager.PurchaseManager;
import com.common.control.model.PurchaseModel;
import com.common.control.utils.AppUtils;
import com.common.control.utils.SharePrefUtils;

import java.util.List;


public abstract class MyApplication extends Application {

    @Override
    public final void onCreate() {
        super.onCreate();
        onApplicationCreate();

        SharePrefUtils.getInstance().init(this);
        AdmobManager.getInstance().init(this, isShowAdsTest() ? AdmobManager.getInstance().getDeviceId(this) : "");
        AdmobManager.getInstance().hasAds(hasAds());
        if (enableAdsResume()) {
            AppOpenManager.getInstance().init(this, getOpenAppAdId());
        }
        AdmobManager.getInstance().setShowLoadingDialog(isShowDialogLoadingAd());

        AppConfig appConfig = getAppConfig();
        AppUtils.getInstance().setAppConfig(appConfig);
        AdmobManager.getInstance().setHasLog(appConfig.isShowLogIdAd());

        if (isInitBilling()) {
            PurchaseManager.getInstance().init(this, getPurchaseList());
        }

        if (hasAdjust()) {
            initAdjust();
        }
    }

    private void initAdjust() {
        AdmobManager.getInstance().hasAdjust(true);
        String environment = BuildConfig.DEBUG ? AdjustConfig.ENVIRONMENT_SANDBOX : AdjustConfig.ENVIRONMENT_PRODUCTION;
        AdjustConfig config = new AdjustConfig(this, getAdjustAppToken(), environment);
        config.setLogLevel(LogLevel.VERBOSE);
        Adjust.onCreate(config);
        registerActivityLifecycleCallbacks(new AdjustLifecycleCallbacks());
    }

    protected abstract void onApplicationCreate();

    protected abstract boolean hasAdjust();

    protected abstract String getAdjustAppToken();

    protected abstract boolean hasAds();

    protected abstract boolean isShowDialogLoadingAd();

    protected abstract boolean isShowAdsTest();

    protected abstract boolean enableAdsResume();

    protected abstract String getOpenAppAdId();

    protected abstract boolean isInitBilling();

    protected abstract List<PurchaseModel> getPurchaseList();

    protected abstract AppConfig getAppConfig();

    private static class AdjustLifecycleCallbacks implements ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {
            Adjust.onResume();
        }

        @Override
        public void onActivityPaused(Activity activity) {
            Adjust.onPause();
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }

    }
}
