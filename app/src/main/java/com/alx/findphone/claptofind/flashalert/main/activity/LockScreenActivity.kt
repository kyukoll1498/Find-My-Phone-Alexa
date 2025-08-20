package com.alx.findphone.claptofind.flashalert.main.activity

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.databinding.ActivityLockScreenBinding
import com.alx.findphone.claptofind.flashalert.main.fragment.HomeFragment
import com.alx.findphone.claptofind.flashalert.utils.app.AppPreferences
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.bumptech.glide.Glide

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
            val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }

        val itemAlert = AppPreferences.Companion.instance.getCurrentItemAlert()
        Glide.with(this).load(itemAlert.imageRaw).into(binding.ivBackground)
        Glide.with(this).load(itemAlert.imageButton).into(binding.btTurnOff)
        binding.tvTitle.setTextColor(ContextCompat.getColor(this, itemAlert.colorText))
    }

    override fun addEvent() {
        binding.btTurnOff.setOnClickListener {
            sendBroadcast(Intent(Constants.ACTION_FINISH_DETECT))
            HomeFragment.Companion.start(this)
            finish()
        }
    }
}