package com.mtg.tool.findmyphone.main.fragment

import androidx.recyclerview.widget.GridLayoutManager
import com.mtg.tool.findmyphone.ADAPTER_ADS_TYPE
import com.mtg.tool.findmyphone.ADAPTER_ITEM_TYPE
import com.mtg.tool.findmyphone.ADS_SOUND_TYPE
import com.mtg.tool.findmyphone.DEFAULT_SOUND_TYPE
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.data.repo.AppRepository
import com.mtg.tool.findmyphone.databinding.FragmentSoundBinding
import com.mtg.tool.findmyphone.main.adapter.SoundAdapter

class SoundFragment : BaseFragment<FragmentSoundBinding>(FragmentSoundBinding::inflate) {
    private lateinit var soundAdapter: SoundAdapter

    override fun initView() {
        setupList()

    }

    private fun setupList() {
        soundAdapter = SoundAdapter(AppRepository.getAllSound(requireContext()), context)
        var gridLayoutManager = GridLayoutManager(context, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (soundAdapter.getItemViewType(position)) {
                    ADAPTER_ADS_TYPE -> 3
                    ADAPTER_ITEM_TYPE -> 1
                    else -> -1
                }
            }
        }
        binding.rcvSound.layoutManager = gridLayoutManager
        binding.rcvSound.adapter = soundAdapter
    }

    override fun addEvent() {

    }
}