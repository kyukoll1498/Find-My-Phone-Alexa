package com.mtg.tool.findmyphone.main.fragment

import com.mtg.tool.findmyphone.MODE_FLASH_DEFAULT
import com.mtg.tool.findmyphone.MODE_FLASH_DISCO
import com.mtg.tool.findmyphone.MODE_FLASH_SOS
import com.mtg.tool.findmyphone.MODE_VIBRATE_DEFAULT
import com.mtg.tool.findmyphone.MODE_VIBRATE_HEART
import com.mtg.tool.findmyphone.MODE_VIBRATE_STRONG
import com.mtg.tool.findmyphone.MODE_VIBRATE_TICKTOCK
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.databinding.FragmentSettingBinding
import com.mtg.tool.findmyphone.utils.app.VibrateFlashThread

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {
    override fun initView() {

    }

    override fun addEvent() {
        binding.rbFlashDefault.setOnClickListener {
            VibrateFlashThread(
                requireContext(),
                MODE_FLASH_DEFAULT
            ).start()
        }
        binding.rbFlashDisco.setOnClickListener {
            VibrateFlashThread(
                requireContext(),
                MODE_FLASH_DISCO
            ).start()
        }
        binding.rbFlashSos.setOnClickListener {
            VibrateFlashThread(
                requireContext(),
                MODE_FLASH_SOS
            ).start()
        }
        binding.rbVibrateDefault.setOnClickListener {
            VibrateFlashThread(
                requireContext(),
                MODE_VIBRATE_DEFAULT
            ).start()
        }
        binding.rbVibrateStrong.setOnClickListener {
            VibrateFlashThread(
                requireContext(),
                MODE_VIBRATE_STRONG
            ).start()
        }
        binding.rbVibrateHeart.setOnClickListener {
            VibrateFlashThread(
                requireContext(),
                MODE_VIBRATE_HEART
            ).start()
        }
        binding.rbVibrateTicktock.setOnClickListener {
            VibrateFlashThread(
                requireContext(),
                MODE_VIBRATE_TICKTOCK
            ).start()
        }
    }

    override fun onPause() {
        super.onPause()
        VibrateFlashThread.stopAll()
    }
}