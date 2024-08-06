package com.mtg.tool.findmyphone.main.activity

import android.graphics.Paint
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.preferences.SharedPrefs
import com.mtg.tool.findmyphone.databinding.ActivityOnboardingBinding
import com.mtg.tool.findmyphone.utils.setSize

class InterestActivity :
    BaseActivity<ActivityOnboardingBinding>(ActivityOnboardingBinding::inflate) {
    override fun binding() {
        isFullScreen = true
        SharedPrefs.put(this, "is_skip_onboard", true)
        super.binding()
    }

    override fun initView() {
        binding.tvNext.setSize(18)
        binding.tvNext.paintFlags = Paint.UNDERLINE_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG

    }

    override fun addEvent() {

    }
}