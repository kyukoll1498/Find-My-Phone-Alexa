package com.alx.findphone.claptofind.flashalert.main.activity

import android.content.Context
import android.content.Intent
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.databinding.ActivityAlertEnableBinding
import com.alx.findphone.claptofind.flashalert.main.fragment.HomeFragment

class AlertEnableActivity : BaseActivity<ActivityAlertEnableBinding>(ActivityAlertEnableBinding::inflate) {

    override fun initView() {
        // Initialize the view components here
    }

    override fun addEvent() {
        binding.ivBackHome.setOnClickListener {
            HomeFragment.Companion.start(this)
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