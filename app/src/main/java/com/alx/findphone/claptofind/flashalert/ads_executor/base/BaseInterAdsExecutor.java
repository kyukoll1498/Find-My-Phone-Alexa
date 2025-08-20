package com.alx.findphone.claptofind.flashalert.ads_executor.base;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.common.control.interfaces.AdCallback;
import com.common.control.manager.AdmobManager;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.alx.findphone.claptofind.flashalert.ads_executor.callback.ShowAdsCallback;

public abstract class BaseInterAdsExecutor {
    protected boolean isLoading = false;
    protected InterstitialAd interAds = null;

    public void loadInterAds(Context context) {
        if (!isLoading && interAds == null) {
            Log.d("RequestAds", "loadInterAds: Request Inter File Ads");
            isLoading = true;
            AdmobManager.getInstance().loadInterAds(context, getAdsId(), new AdCallback() {
                @Override
                public void onResultInterstitialAd(InterstitialAd interstitialAd) {
                    super.onResultInterstitialAd(interstitialAd);
                    isLoading = false;
                    interAds = interstitialAd;
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError i) {
                    super.onAdFailedToLoad(i);
                    isLoading = false;
                    interAds = null;
                }
            });
        }
    }

    public void showInterAds(Activity activity, ShowAdsCallback callback) {
        AdmobManager.getInstance().showInterstitial(activity, interAds, new AdCallback() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                callback.onAdsClose();
            }
        });
        interAds = null;
        loadInterAds(activity);
    }

    protected abstract String getAdsId();
}
