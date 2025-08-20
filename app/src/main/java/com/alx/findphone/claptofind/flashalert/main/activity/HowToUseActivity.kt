package com.alx.findphone.claptofind.flashalert.main.activity

import android.util.Log
import com.alx.findphone.claptofind.flashalert.AdIds
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.databinding.ActivityHowToUseBinding
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.google.android.gms.ads.nativead.NativeAd

class HowToUseActivity : BaseActivity<ActivityHowToUseBinding>(ActivityHowToUseBinding::inflate) {
    private val listNative by lazy {
        arrayListOf(AdIds.native_tutorial_high, AdIds.native_tutorial)
    }
    private var isFirstLoad = true;

    override fun initView() {
        firstLoad()
//        binding.composeView.setContent {
//            HowToUseScreen()
//        }
    }

    private fun firstLoad() {
        if (isFirstLoad) {
            loadAlternateNative()
        }
    }

    private fun loadAlternateNative() {
        Log.d("Refresh", "RefreshHowToUse")
        AdmobManager.getInstance().preloadAlternateNative(
            this, listNative, object : AdCallback() {
                override fun onNativeAds(nativeAd: NativeAd?) {
                    super.onNativeAds(nativeAd)
                    AdmobManager.getInstance().showNative(
                        this@HowToUseActivity, nativeAd, binding.frAd, AdmobManager.NativeAdType.SMALL
                    )
                    Log.d("Refresh", "ShowRefreshHowToUse")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                }
            })
    }

    override fun addEvent() {
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    override fun onResume() {
        super.onResume()
        if (!AppOpenManager.getInstance().isShowingAd) {
            if (!isFirstLoad) {
                loadAlternateNative()
            }
        }
        isFirstLoad = false
    }
}
