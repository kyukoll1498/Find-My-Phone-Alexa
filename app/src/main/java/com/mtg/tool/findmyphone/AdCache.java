package com.mtg.tool.findmyphone;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;

public class AdCache {

    private static AdCache instance;
    private int interSoundCount = 0;

    private InterstitialAd interOpenTopic;
    private InterstitialAd interOpenSoundPlay;
    private InterstitialAd interGuide;
    private RewardedAd rewardedAd;

    private InterstitialAd interBackTopic;
    private InterstitialAd interBackSoundPlay;

    private AdCache() {
    }

    public InterstitialAd getInterOpenSoundPlay() {
        return interOpenSoundPlay;
    }

    public void setInterOpenSoundPlay(InterstitialAd interOpenSoundPlay) {
        this.interOpenSoundPlay = interOpenSoundPlay;
    }

    public static AdCache getInstance() {
        if (instance == null) {
            instance = new AdCache();
        }
        return instance;
    }

    public InterstitialAd getInterOpenTopic() {
        return interOpenTopic;
    }

    public void setInterOpenTopic(InterstitialAd interOpenTopic) {
        this.interOpenTopic = interOpenTopic;
    }

    public RewardedAd getRewardedAd() {
        return rewardedAd;
    }

    public void setRewardedAd(RewardedAd rewardedAd) {
        this.rewardedAd = rewardedAd;
    }

    public InterstitialAd getInterGuide() {
        return interGuide;
    }

    public void setInterGuide(InterstitialAd interGuide) {
        this.interGuide = interGuide;
    }

    public InterstitialAd getInterBackTopic() {
        return interBackTopic;
    }

    public void setInterBackTopic(InterstitialAd interBackTopic) {
        this.interBackTopic = interBackTopic;
    }

    public InterstitialAd getInterBackSoundPlay() {
        return interBackSoundPlay;
    }

    public void setInterBackSoundPlay(InterstitialAd interBackSoundPlay) {
        this.interBackSoundPlay = interBackSoundPlay;
    }
}
