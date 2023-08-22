package com.mtg.tool.findmyphone;

import com.google.android.gms.ads.interstitial.InterstitialAd;

public class AdCache {

    private static AdCache instance;
    private int interSoundCount = 0;

    private InterstitialAd interSound;

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
