package com.alx.findphone.claptofind.flashalert.main.activity

import android.content.Context
import android.content.Intent
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.databinding.ActivityAlertTutorialBinding
import com.bumptech.glide.Glide

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