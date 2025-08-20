package com.alx.findphone.claptofind.flashalert.main.activity

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.data.model.ItemAlert
import com.alx.findphone.claptofind.flashalert.databinding.ActivityAlertApplyBinding
import com.alx.findphone.claptofind.flashalert.utils.app.AppPreferences
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.bumptech.glide.Glide

class AlertApplyActivity : BaseActivity<ActivityAlertApplyBinding>(ActivityAlertApplyBinding::inflate) {
    private lateinit var itemAlert: ItemAlert

    companion object {
        @JvmStatic
        fun start(context: Context, itemAlert: ItemAlert) {
            val starter = Intent(context, AlertApplyActivity::class.java)
            starter.putExtra("data", itemAlert)
            context.startActivity(starter)
        }
    }

    override fun initView() {
        // Initialize the view components here
        itemAlert = intent.getSerializableExtra("data") as ItemAlert
        Glide.with(this).load(itemAlert.imageRaw).into(binding.ivImage)
        Glide.with(this).load(itemAlert.imageButton).into(binding.btTurnoff)
        binding.tvTitle.setTextColor(ContextCompat.getColor(this, itemAlert.colorText))

    }

    override fun addEvent() {
        // Add event listeners here
        binding.llApply.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                AlertPermissionActivity.start(this)
            } else {
                itemAlert.isSelected = false
                sendBroadcast(Intent(Constants.APPLY_ACTIVE_CLAP))
                AppPreferences.Companion.instance.setCurrentItemAlert(itemAlert)
                AlertEnableActivity.start(this)
            }
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}