package com.mtg.tool.findmyphone.main.activity

import android.os.Bundle
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.common.control.manager.TrackRevenueSolar
import com.google.android.gms.ads.nativead.NativeAd
import com.mtg.tool.findmyphone.AdIds
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.databinding.ActivityHowToUseBinding
import com.mtg.tool.findmyphone.main.activity.jpcompose.HowToUseScreen

class HowToUseActivity : BaseActivity<ActivityHowToUseBinding>(ActivityHowToUseBinding::inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hiển thị HowToUseScreen trong ComposeView
        binding.composeView.setContent {
            HowToUseScreen()
        }
        R.layout.activity_how_to_use
        initView()
        addEvent()
    }

    override fun initView() {
        AdmobManager.getInstance().loadNative(this, AdIds.native_tutorial, binding.frAd,AdmobManager.NativeAdType.SMALL)
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(this, binding.frAd)
    }

    override fun addEvent() {
        binding.btnBack.setOnClickListener { onBackPressed() }
    }
}
