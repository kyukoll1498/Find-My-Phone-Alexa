package com.alx.findphone.claptofind.flashalert.main.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Shader.TileMode
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.databinding.ActivityTryUninstallBinding

class UninstallActivity : BaseActivity<ActivityTryUninstallBinding>(ActivityTryUninstallBinding::inflate) {

    override fun initView() {
//        val textShader: Shader = LinearGradient(
//            0f,
//            0f,
//            0f,
//            20f,
//            intArrayOf(Color.parseColor("#3c92e3"), Color.parseColor("#25d9c0")),
//            floatArrayOf(0f, 1f),
//            TileMode.CLAMP
//        )
//        binding.tvTryAgain.paint.shader = textShader
//        binding.tvStillUninstall.paint.shader = textShader
    }

    override fun addEvent() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvTryAgain.setOnClickListener {
            MainActivity.startFromUninstall(this)
            finish()
        }
        binding.tvStillUninstall.setOnClickListener {
            IssueUninstallActivity.start(this)
            finish()
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, UninstallActivity::class.java)
            context.startActivity(starter)
        }
    }
}