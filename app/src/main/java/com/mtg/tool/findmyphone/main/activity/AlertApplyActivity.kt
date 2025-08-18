package com.mtg.tool.findmyphone.main.activity

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.bumptech.glide.Glide
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.model.ItemAlert
import com.mtg.tool.findmyphone.databinding.ActivityAlertApplyBinding
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import com.mtg.tool.findmyphone.utils.constant.Constants

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
        binding.tvTitle.setTextColor(itemAlert.colorText)

    }

    override fun addEvent() {
        // Add event listeners here
        binding.llApply.setOnClickListener {
            if (!Settings.canDrawOverlays(this)) {
                AlertPermissionActivity.start(this)
            } else {
                itemAlert.isSelected = false
                sendBroadcast(Intent(Constants.APPLY_ACTIVE_CLAP))
                AppPreferences.instance.setCurrentItemAlert(itemAlert)
                AlertEnableActivity.start(this)
            }
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
    }
}