package com.example.voicelockscreen.ads_executor.inter

import android.app.Activity
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.ads_executor.base.BaseInterAdsExecutor
import com.mtg.tool.findmyphone.ads_executor.callback.ShowAdsCallback
import com.mtg.tool.findmyphone.utils.EventLogger

object InterSplashExecutor : BaseInterAdsExecutor() {
    override fun getAdsId(): String {
        return BuildConfig.inter_splash
    }

    fun showInterAds(
        activity: Activity?,
        onBeforeShowCallback: OnBeforeShowCallback,
        callback: ShowAdsCallback?
    ) {
        if (isLoading) {
            val thread = Thread {
                while (isLoading) {
                    Thread.sleep(50)
                }

                activity?.runOnUiThread {
                    onBeforeShowCallback.callback()
                    show(activity, callback)
                }
            }
            thread.start()
        } else {
            onBeforeShowCallback.callback()
            show(activity, callback)
        }
    }

    private fun show(activity: Activity?, callback: ShowAdsCallback?) {
        if (interAds != null){
            EventLogger.getInstance()?.logEvent("open_splash_with_ad")
        }else{
            EventLogger.getInstance()?.logEvent("open_splash_without_ad")
        }

        AdmobManager.getInstance().showInterstitial(activity, interAds, object : AdCallback() {
            override fun onAdClosed() {
                super.onAdClosed()
                callback!!.onAdsClose()
            }
        })
        interAds = null
    }

    interface OnBeforeShowCallback {
        fun callback()
    }
}