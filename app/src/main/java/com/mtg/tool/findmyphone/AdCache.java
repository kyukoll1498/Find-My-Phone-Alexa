package com.mtg.tool.findmyphone;

import androidx.lifecycle.MutableLiveData;

import com.facebook.internal.Mutable;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;

import org.jetbrains.annotations.Nullable;

public class AdCache {

    private static AdCache instance;

    public NativeAd lfo2NativeHigh = null;
    public NativeAd lfo2NativeHigh1 = null;
    public NativeAd lfo2NativeHigh2 = null;
    public NativeAd ob4NativeHigh = null;
    public NativeAd ob4NativeHigh2AndNative = null;
    public NativeAd ob5NativeHigh = null;
    public NativeAd ob6NativeHigh = null;
    public NativeAd ob4NativeHigh1 = null;
    public NativeAd ob2NativeHigh = null;

    public NativeAd ob1Native = null;
    public MutableLiveData<NativeAd> lfo1Native = new MutableLiveData<>(null);


    private AdCache() {
    }

    public static AdCache getInstance() {
        if (instance == null) {
            instance = new AdCache();
        }
        return instance;
    }

}
