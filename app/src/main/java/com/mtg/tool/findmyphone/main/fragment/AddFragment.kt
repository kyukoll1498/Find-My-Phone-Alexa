package com.mtg.tool.findmyphone.main.fragment

import android.view.View
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.databinding.FragmentAddBinding

class AddFragment  : BaseFragment<FragmentAddBinding>(FragmentAddBinding::inflate) {
    override fun initView() {
    }

    private fun setUpResponsive() {
        if (binding.tvEmpty.y + binding.tvEmpty.height > binding.ivGuideCreate.y) {
            binding.ivGuideCreate.visibility = View.GONE
        }
    }

    override fun addEvent() {

    }

    override fun onResume() {
        super.onResume()
        setUpResponsive()
    }
}