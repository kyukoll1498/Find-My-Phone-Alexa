package com.mtg.tool.findmyphone.main.activity

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.databinding.ActivityLockScreenBinding
import com.mtg.tool.findmyphone.main.fragment.HomeFragment
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import com.mtg.tool.findmyphone.utils.constant.Constants

class LockScreenActivity : BaseActivity<ActivityLockScreenBinding>(ActivityLockScreenBinding::inflate) {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, LockScreenActivity::class.java).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            context.startActivity(starter)
        }
    }

    override fun initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }

        val itemAlert = AppPreferences.instance.getCurrentItemAlert()
        Glide.with(this).load(itemAlert.imageRaw).into(binding.ivBackground)
        Glide.with(this).load(itemAlert.imageButton).into(binding.btTurnOff)
        binding.tvTitle.setTextColor(ContextCompat.getColor(this, itemAlert.colorText))
    }

    override fun addEvent() {
        binding.btTurnOff.setOnClickListener {
            sendBroadcast(Intent(Constants.ACTION_FINISH_DETECT))
            HomeFragment.start(this)
            finish()
        }
    }
}