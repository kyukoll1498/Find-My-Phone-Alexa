package com.alx.findphone.claptofind.flashalert.main.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.databinding.ActivityAlertPermissionBinding

class AlertPermissionActivity : BaseActivity<ActivityAlertPermissionBinding>(ActivityAlertPermissionBinding::inflate) {

    override fun initView() {
        // Initialize the view components here
    }

    override fun addEvent() {
        binding.ivCancel.setOnClickListener {
            finish()
        }
        binding.ivAllow.setOnClickListener {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${this.packageName}")
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, AlertPermissionActivity::class.java)
            context.startActivity(starter)
        }
    }
}