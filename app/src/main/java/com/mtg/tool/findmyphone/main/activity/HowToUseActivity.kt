package com.mtg.tool.findmyphone.main.activity

import com.common.control.manager.AdmobManager
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.databinding.ActivityCreateSoundBinding
import com.mtg.tool.findmyphone.databinding.ActivityHowToUseBinding

class HowToUseActivity: BaseActivity<ActivityHowToUseBinding>(ActivityHowToUseBinding::inflate) {
    override fun initView() {
        AdmobManager.getInstance().loadCollapsibleBanner(this, BuildConfig.collapsible_banner_how_to_use, binding.frAd)

    }

    override fun addEvent() {
        binding.btnBack.setOnClickListener { onBackPressed() }
    }
}