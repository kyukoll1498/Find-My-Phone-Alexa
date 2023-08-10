package com.mtg.tool.findmyphone.main.fragment

import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.databinding.FragmentOnboadingBinding
import com.mtg.tool.findmyphone.utils.setSize


class OnBoardFragment(
    var idImage: Int = R.drawable.img_inside_1,
    var idText: Int = R.string.text_on_boarding_1
) : BaseFragment<FragmentOnboadingBinding>(FragmentOnboadingBinding::inflate) {


    override fun initView() {
        binding.imgInside.setImageResource(idImage)
        binding.tvInside.text = getString(idText)
        binding.tvInside.setSize(20)
    }

    override fun addEvent() {

    }

}