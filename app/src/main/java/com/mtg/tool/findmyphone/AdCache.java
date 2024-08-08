package com.mtg.tool.findmyphone;

import androidx.lifecycle.MutableLiveData;

import com.facebook.internal.Mutable;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.nativead.NativeAd;

public class AdCache {

    private static AdCache instance;
    private int interSoundCount = 0;

    private InterstitialAd interSound;

    public NativeAd lfo2NativeHigh = null;
    public NativeAd lfo2NativeHigh1 = null;
    public NativeAd lfo2NativeHigh2 = null;

    public NativeAd ob4NativeHigh = null;
    public NativeAd ob5NativeHigh = null;
    public NativeAd ob4NativeHigh1 = null;
    public NativeAd ob2NativeHigh = null;
    public MutableLiveData<NativeAd> lfo1Native = new MutableLiveData<>(null);


    private AdCache() {
    }

    public static AdCache getInstance() {
        if (instance == null) {
            instance = new AdCache();
        }
        return instance;
    }

    public InterstitialAd getInterSound() {
        return interSound;
    }

    public void setInterSound(InterstitialAd interSound) {
        this.interSound = interSound;
    }

    public void pullCountInterSound() {
        interSoundCount++;
    }

    public boolean canShowInterSound() {
        return interSoundCount%2 != 0;
    }

}
