package com.mtg.tool.findmyphone.main.fragment

import android.content.Intent
import android.view.View
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.databinding.FragmentAddBinding
import com.mtg.tool.findmyphone.main.activity.CreateSoundActivity

class AddFragment  : BaseFragment<FragmentAddBinding>(FragmentAddBinding::inflate) {
    override fun initView() {
    }

    private fun setUpResponsive() {
        if (binding.tvEmpty.y + binding.tvEmpty.height > binding.ivGuideCreate.y) {
            binding.ivGuideCreate.visibility = View.GONE
        }
    }

    override fun addEvent() {
        binding.llCreateSound.setOnClickListener {
            startActivity(Intent(activity, CreateSoundActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        setUpResponsive()
    }
}