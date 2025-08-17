package com.mtg.tool.findmyphone.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.databinding.ActivityIssueUninstallBinding

class IssueUninstallActivity : BaseActivity<ActivityIssueUninstallBinding>(ActivityIssueUninstallBinding::inflate) {
    private var state1: Boolean = false
    private var state2: Boolean = false
    private var state3: Boolean = false

    override fun initView() {

    }

    override fun addEvent() {
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.tvCancel.setOnClickListener {
            finish()
        }
        binding.tvUninstall.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:${packageName}")
            startActivity(intent)
        }
        binding.llState1.setOnClickListener {
            state1 = !state1
            binding.rbState1.isChecked = state1
        }
        binding.rbState1.setOnClickListener {
            state1 = !state1
            binding.rbState1.isChecked = state1
        }
        binding.llState2.setOnClickListener {
            state2 = !state2
            binding.rbState2.isChecked = state2
        }
        binding.rbState2.setOnClickListener {
            state2 = !state2
            binding.rbState2.isChecked = state2
        }
        binding.llState3.setOnClickListener {
            state3 = !state3
            binding.rbState3.isChecked = state3
        }
        binding.rbState3.setOnClickListener {
            state3 = !state3
            binding.rbState3.isChecked = state3
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, IssueUninstallActivity::class.java)
            context.startActivity(starter)
        }
    }
}