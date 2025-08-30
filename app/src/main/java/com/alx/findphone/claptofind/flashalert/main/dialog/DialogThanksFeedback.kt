package com.alx.findphone.claptofind.flashalert.main.dialog

import android.content.Context
import android.content.Intent
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.databinding.DialogThanksFeedbackBinding

class DialogThanksFeedback : BaseActivity<DialogThanksFeedbackBinding>(DialogThanksFeedbackBinding::inflate) {
    companion object{
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, DialogThanksFeedback::class.java)
            context.startActivity(starter)
        }
    }

    override fun initView() {
    }

    override fun addEvent() {
        binding.tvRate.setOnClickListener {
            finish()
        }
    }

}