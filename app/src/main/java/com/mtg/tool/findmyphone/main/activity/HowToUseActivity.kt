package com.mtg.tool.findmyphone.main.activity

import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.databinding.ActivityCreateSoundBinding
import com.mtg.tool.findmyphone.databinding.ActivityHowToUseBinding

class HowToUseActivity: BaseActivity<ActivityHowToUseBinding>(ActivityHowToUseBinding::inflate) {
    override fun initView() {
        AdmobManager.getInstance().loadBanner(this, BuildConfig.banner_tutorial, binding.frAd)
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(this, binding.frAd)

    }

    override fun addEvent() {
        binding.btnBack.setOnClickListener { onBackPressed() }
    }
}