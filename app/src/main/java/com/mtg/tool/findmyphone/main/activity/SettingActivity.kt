package com.mtg.tool.findmyphone.main.activity

import android.content.Intent
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.preferences.SharedPrefs
import com.mtg.tool.findmyphone.databinding.ActivitySettingBinding
import com.mtg.tool.findmyphone.utils.ActionUtils
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.hide

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate)  {
    override fun initView() {
        setUpRate()
        setupDrawerNavigation()
    }

    override fun addEvent() {
    }

    private fun setupDrawerNavigation() {

        binding.btnLanguage.setOnClickListener {
            EventLogger.getInstance()?.logEvent("click_set_language")
            startActivity(Intent(this, Language2Activity::class.java))
        }
        binding.btnRateNavigation.setOnClickListener {
            EventLogger.getInstance()?.logEvent("click_set_rate")
            ActionUtils.showRateDialog(this, false, callback = {
                if (it) hideRate()
            })
        }
        binding.btnShare.setOnClickListener {
            EventLogger.getInstance()?.logEvent("click_set_share")
            ActionUtils.shareApp(this)
        }
        binding.btnFeedback.setOnClickListener {
            ActionUtils.sendFeedback(this)
        }
        binding.btnPrivacy.setOnClickListener {
            PolicyWebViewActivity.start(this)
        }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun hideRate() {
        binding.btnRateNavigation.hide()
    }

    private fun setUpRate() {
        if (SharedPrefs.isRated(this)) {
            hideRate()
        }
    }
}