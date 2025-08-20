package com.alx.findphone.claptofind.flashalert.ads_executor.inter

import android.app.Activity
import com.alx.findphone.claptofind.flashalert.BuildConfig
import com.alx.findphone.claptofind.flashalert.ads_executor.base.BaseInterAdsExecutor
import com.alx.findphone.claptofind.flashalert.ads_executor.callback.ShowAdsCallback
import com.alx.findphone.claptofind.flashalert.utils.EventLogger
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager

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
        if (interAds != null) {
            EventLogger.Companion.getInstance()?.logEvent("open_splash_with_ad")
        } else {
            EventLogger.Companion.getInstance()?.logEvent("open_splash_without_ad")
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