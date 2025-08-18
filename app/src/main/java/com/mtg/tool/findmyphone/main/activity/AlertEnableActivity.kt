package com.mtg.tool.findmyphone.main.activity

import android.content.Context
import android.content.Intent
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.databinding.ActivityAlertEnableBinding
import com.mtg.tool.findmyphone.main.fragment.HomeFragment

class AlertEnableActivity : BaseActivity<ActivityAlertEnableBinding>(ActivityAlertEnableBinding::inflate) {

    override fun initView() {
        // Initialize the view components here
    }

    override fun addEvent() {
        binding.ivBackHome.setOnClickListener {
            HomeFragment.start(this)
            finish()
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, AlertEnableActivity::class.java)
            context.startActivity(starter)
        }
    }
}