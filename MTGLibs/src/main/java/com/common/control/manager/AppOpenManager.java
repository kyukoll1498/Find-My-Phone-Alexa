package com.common.control.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.common.control.dialog.WelcomeBackDialog;
import com.common.control.interfaces.AdCallback;
import com.google.android.gms.ads.AdActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private static final String TAG = "AppOpenManager";

    @SuppressLint("StaticFieldLeak")
    private static volatile AppOpenManager INSTANCE;
    private final List<Class> disabledAppOpenList;

    private final List<Class> disabledAppOpenInRewardList;
    private AppOpenAd appResumeAd = null;
    private String appResumeAdId;
    private Activity currentActivity;
    private Application myApplication;
    private boolean isShowingAd = false;
    private boolean isInitialized = false;
    private boolean isAppResumeEnabled = true;
    private String ACTION_DISMISS_NATIVE = "ACTION_DISMISS_NATIVE";
    private String ACTION_SHOW_NATIVE = "ACTION_SHOW_NATIVE";

    private long loadTime;
    private WelcomeBackDialog dialog;
    private long timeShowLoading = 100;
    private FrameLayout frAd;
    private List<FrameLayout> frAds = new ArrayList<>();

    private BroadcastReceiver receiverShowAd = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_DISMISS_NATIVE)) {
                for (FrameLayout frAd : frAds) {
                    try {
                        frAd.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (intent.getAction().equals(ACTION_SHOW_NATIVE)) {
                for (FrameLayout frAd : frAds) {
                    try {
                        frAd.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    private AppOpenManager() {
        disabledAppOpenList = new ArrayList<>();
        disabledAppOpenInRewardList = new ArrayList<>();
    }

    public static synchronized AppOpenManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppOpenManager();
        }
        return INSTANCE;
    }

    public void setTimeShowLoading(long timeShowLoading) {
        this.timeShowLoading = timeShowLoading;
    }

    public AppOpenAd getAppResumeAd() {
        return appResumeAd;
    }

    public String getAppResumeAdId() {
        return appResumeAdId;
    }

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public Application getMyApplication() {
        return myApplication;
    }

    public boolean isShowingOpenAd() {
        return isShowingAd;
    }

    public boolean isAppResumeEnabled() {
        return isAppResumeEnabled;
    }

    public List<Class> getDisabledAppOpenList() {
        return disabledAppOpenList;
    }

    public List<Class> getDisabledAppOpenInRewardList() {
        return disabledAppOpenInRewardList;
    }

    public long getLoadTime() {
        return loadTime;
    }

    public void init(Application application, String appOpenAdId) {
        isInitialized = true;
        this.myApplication = application;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        this.appResumeAdId = appOpenAdId;
    }

    public boolean isInitialized() {
        return isInitialized;
    }


    public void disableAppResumeWithActivity(Class activityClass) {
        Log.d(TAG, "disableAppResumeWithActivity: " + activityClass.getName());
        disabledAppOpenList.add(activityClass);
    }

    public void disableAppResumeWithRewardActivity(Class activityClass) {
        Log.d(TAG, "disableAppResumeWithActivity: " + activityClass.getName());
        disabledAppOpenInRewardList.add(activityClass);
    }

    public void enableAppResumeWithActivity(Class activityClass) {
        Log.d(TAG, "enableAppResumeWithActivity: " + activityClass.getName());
        disabledAppOpenList.remove(activityClass);
    }

    public void disableAppResume() {
        isAppResumeEnabled = false;
    }

    public void enableAppResume() {
        isAppResumeEnabled = true;
    }


    public void fetchAd() {
        fetchAd(null);
    }

    public void fetchAd(AdCallback callback) {
        if (isAdAvailable()) {
            if (callback != null) callback.onAdLoaded();
            return;
        }
        AdRequest request = AdmobManager.getInstance().getAdRequest();
        if (request == null) {
            return;
        }

        AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AppOpenAd ad) {
                AppOpenManager.this.appResumeAd = ad;
                AppOpenManager.this.loadTime = (new Date()).getTime();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                AppOpenManager.this.appResumeAd = null;
            }
        };
        AdmobManager.getInstance().log("Request OpenAd :" + appResumeAdId);
        AppOpenAd.load(myApplication, appResumeAdId, request, AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
        long dateDifference = (new Date()).getTime() - this.loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    public boolean isAdAvailable() {
        return appResumeAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        currentActivity = activity;
        fetchAd();
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
//        try {
//            currentActivity.unregisterReceiver(receiverShowAd);
//        }catch (Exception e){
//
//        }
        currentActivity = null;

    }


    public void showAdIfAvailable() {
        if (currentActivity != null && !AdmobManager.getInstance().isHasAds()) {
            return;
        }

        Log.d(TAG, "showAdIfAvailable: " + ProcessLifecycleOwner.get().getLifecycle().getCurrentState());
        if (!ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            Log.d(TAG, "showAdIfAvailable: return");
            return;
        }
        if (isAdAvailable()) {
            Log.d(TAG, "Will show ad.");
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    AppOpenManager.this.appResumeAd = null;
                    isShowingAd = false;
//                            fetchAd();
                    dismissDialogLoading();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            currentActivity.sendBroadcast(new Intent(ACTION_SHOW_NATIVE));
                        }
                    }, 100);
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    dismissDialogLoading();
                    AppOpenManager.this.appResumeAd = null;
                    fetchAd();
                    if (adError.getCode() != 0)
                        currentActivity.sendBroadcast(new Intent(ACTION_SHOW_NATIVE));
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    isShowingAd = true;
                }
            };
            showAdsWithLoading(fullScreenContentCallback);
        }
    }

    private void dismissDialogLoading() {
        try {
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAdsWithLoading(FullScreenContentCallback fullScreenContentCallback) {
        if (isShowingAd || appResumeAd == null) {
            return;
        }
        if (ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
//            try {
//                dismissDialogLoading();
//                dialog = new WelcomeBackDialog(currentActivity);
//                dialog.show();
//            } catch (Exception e) {
//                dialog = null;
//                e.printStackTrace();
//            }
//            final Dialog finalDialog = dialog;

            if (fullScreenContentCallback != null) {
                appResumeAd.setFullScreenContentCallback(fullScreenContentCallback);
            }
            appResumeAd.setOnPaidEventListener(adValue -> AdmobManager.getInstance().trackRevenue(adValue));
            appResumeAd.show(currentActivity);
            currentActivity.sendBroadcast(new Intent(ACTION_DISMISS_NATIVE));

//            new Handler().postDelayed(() -> {
//                if (dialog != null && dialog.isShowing()) {
//                    AdmobManager.getInstance().log("Show OpenAd :" + appResumeAdId);
//                    appResumeAd.show(currentActivity);
//                    currentActivity.sendBroadcast(new Intent("ACTION_DISMISS"));
//                }
//            }, timeShowLoading);
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onResume() {
        if (!isAppResumeEnabled /*|| !AdsConfigManager.getInstance().hasAds(R.string.oa_return)*/) {
            Log.d(TAG, "onResume: app resume is disabled");
            return;
        }

//        for (Class activity : disabledAppOpenInRewardList) {
//            if (activity.getName().equals(currentActivity.getClass().getName()) && SharePrefUtils.getInstance().getBoolean(SharePrefUtils.SHARE_PREF_REWARD_TRANSLATOR_SHOWING, false)) {
//                Log.d(TAG, "onStart: activity is disabled");
//                return;
//            }
//        }

        for (Class activity : disabledAppOpenList) {
            if (activity.getName().equals(currentActivity.getClass().getName())) {
                Log.d(TAG, "onStart: activity is disabled");
                return;
            }
        }

        if (!currentActivity.getClass().getName().equals(AdActivity.class.getName())) {
            showAdIfAvailable();
        }
    }

    public void hideNativeOrBannerWhenShowOpenApp(Activity activity, FrameLayout frAd) {
        this.frAd = frAd;
        if (!frAds.contains(frAd)) {
            frAds.add(frAd);
        }
        IntentFilter filterShowAd = new IntentFilter();
        filterShowAd.addAction(ACTION_DISMISS_NATIVE);
        filterShowAd.addAction(ACTION_SHOW_NATIVE);
        activity.registerReceiver(receiverShowAd, filterShowAd);
    }
}

