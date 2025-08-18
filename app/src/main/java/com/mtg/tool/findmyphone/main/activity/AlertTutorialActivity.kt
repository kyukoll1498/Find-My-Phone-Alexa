package com.mtg.tool.findmyphone.main.activity

import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.databinding.ActivityAlertTutorialBinding

class AlertTutorialActivity : BaseActivity<ActivityAlertTutorialBinding>(ActivityAlertTutorialBinding::inflate) {

    override fun initView() {
        Glide.with(this).load(R.drawable.bg_tutorial).into(binding.ivTutorial)
    }

    override fun addEvent() {
        binding.ivOk.setOnClickListener {
            finish()
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, AlertTutorialActivity::class.java)
            context.startActivity(starter)
        }
    }
}