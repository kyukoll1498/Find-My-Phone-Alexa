package com.mtg.tool.findmyphone.main.fragment

import android.util.Log
import android.view.View
import com.mtg.tool.findmyphone.MODE_FLASH_DEFAULT
import com.mtg.tool.findmyphone.MODE_FLASH_DISCO
import com.mtg.tool.findmyphone.MODE_FLASH_SOS
import com.mtg.tool.findmyphone.MODE_VIBRATE_DEFAULT
import com.mtg.tool.findmyphone.MODE_VIBRATE_HEART
import com.mtg.tool.findmyphone.MODE_VIBRATE_STRONG
import com.mtg.tool.findmyphone.MODE_VIBRATE_TICKTOCK
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.databinding.FragmentSettingBinding
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import com.mtg.tool.findmyphone.utils.app.VibrateFlashThread

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {
    private var appPreferences = AppPreferences.instance

    override fun initView() {
        setUpSelection()
    }

    private fun setUpSelection() {
        when (appPreferences.currentFlash) {
            MODE_FLASH_DEFAULT -> {
                binding.rbFlashDefault.isChecked = true
            }

            MODE_FLASH_DISCO -> {
                binding.rbFlashDisco.isChecked = true
            }

            MODE_FLASH_SOS -> {
                binding.rbFlashSos.isChecked = true
            }
        }

        when (appPreferences.currentVibrate) {
            MODE_VIBRATE_DEFAULT -> {
                binding.rbVibrateDefault.isChecked = true
            }

            MODE_VIBRATE_STRONG -> {
                binding.rbVibrateStrong.isChecked = true
            }

            MODE_VIBRATE_HEART -> {
                binding.rbVibrateHeart.isChecked = true
            }

            MODE_VIBRATE_TICKTOCK -> {
                binding.rbVibrateTicktock.isChecked = true
            }
        }

        binding.sbSound.isChecked = appPreferences.hasSound
        if (appPreferences.hasFlash) {
            binding.sbFlash.isChecked = true
            unlockFlash()
        } else {
            binding.sbFlash.isChecked = false
            lockFlash()
        }
        if (appPreferences.hasVibrate) {
            binding.sbVibrate.isChecked = true
            unlockVibrate()
        } else {
            binding.sbVibrate.isChecked = false
            lockVibrate()
        }
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
        binding.llFlashController.setOnClickListener {
            binding.sbFlash.isChecked = !binding.sbFlash.isChecked
        }
        binding.sbFlash.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                lockFlash()
            } else {
                unlockFlash()
            }
        }

        binding.llVibrateController.setOnClickListener {
            binding.sbVibrate.isChecked = !binding.sbVibrate.isChecked
        }
        binding.sbVibrate.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                lockVibrate()
            } else {
                unlockVibrate()
            }
        }
        binding.llSoundController.setOnClickListener {
            binding.sbSound.isChecked = !binding.sbVibrate.isChecked
        }
        binding.vLockFlash.setOnClickListener { Log.e("android_log_error", "it is disabled") }
        binding.vLockVibrate.setOnClickListener { Log.e("android_log_error", "it is disabled") }
    }

    private fun unlockFlash() {
        binding.rgFlash.alpha = 1F
        binding.vLockFlash.visibility = View.GONE
    }

    private fun lockFlash() {
        VibrateFlashThread.stopFlash()
        binding.rgFlash.alpha = 0.3F
        binding.vLockFlash.visibility = View.VISIBLE
    }

    private fun unlockVibrate() {
        binding.rgVibrate.alpha = 1F
        binding.vLockVibrate.visibility = View.GONE
    }

    private fun lockVibrate() {
        VibrateFlashThread.stopVibrate()
        binding.rgVibrate.alpha = 0.3F
        binding.vLockVibrate.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        VibrateFlashThread.stopAll()
        appPreferences.hasSound = binding.sbSound.isChecked
        appPreferences.hasFlash = binding.sbFlash.isChecked
        appPreferences.hasVibrate = binding.sbVibrate.isChecked

    }
}