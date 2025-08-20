package com.alx.findphone.claptofind.flashalert.main.activity

import android.content.Intent
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.data.preferences.SharedPrefs
import com.alx.findphone.claptofind.flashalert.databinding.ActivitySettingBinding
import com.alx.findphone.claptofind.flashalert.utils.ActionUtils
import com.alx.findphone.claptofind.flashalert.utils.EventLogger
import com.alx.findphone.claptofind.flashalert.utils.hide

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate)  {
    override fun initView() {
        setUpRate()
        setupDrawerNavigation()
    }

    override fun addEvent() {
    }

    private fun setupDrawerNavigation() {

        binding.btnLanguage.setOnClickListener {
            EventLogger.Companion.getInstance()?.logEvent("click_set_language")
            startActivity(Intent(this, Language2Activity::class.java))
        }
        binding.btnRateNavigation.setOnClickListener {
            EventLogger.Companion.getInstance()?.logEvent("click_set_rate")
            ActionUtils.showRateDialog(this, false, callback = {
                if (it) hideRate()
            })
        }
        binding.btnShare.setOnClickListener {
            EventLogger.Companion.getInstance()?.logEvent("click_set_share")
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