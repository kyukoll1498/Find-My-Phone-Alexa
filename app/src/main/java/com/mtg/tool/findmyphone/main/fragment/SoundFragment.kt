package com.mtg.tool.findmyphone.main.fragment

import androidx.recyclerview.widget.GridLayoutManager
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.data.repo.AppRepository
import com.mtg.tool.findmyphone.databinding.FragmentHomeBinding
import com.mtg.tool.findmyphone.databinding.FragmentSoundBinding
import com.mtg.tool.findmyphone.main.adapter.SoundAdapter

class SoundFragment : BaseFragment<FragmentSoundBinding>(FragmentSoundBinding::inflate) {
    private lateinit var soundAdapter: SoundAdapter

    override fun initView() {
        soundAdapter = SoundAdapter(AppRepository.getAllSound(requireContext()), context)
        var gridLayoutManager = GridLayoutManager(context, 3)
        binding.rcvSound.layoutManager = gridLayoutManager
        binding.rcvSound.adapter = soundAdapter
    }

    override fun addEvent() {

    }
}