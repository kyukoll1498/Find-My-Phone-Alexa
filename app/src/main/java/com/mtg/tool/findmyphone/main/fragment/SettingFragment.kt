package com.mtg.tool.findmyphone.main.fragment

import android.content.Intent
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
import com.mtg.tool.findmyphone.data.preferences.SharedPrefs
import com.mtg.tool.findmyphone.databinding.FragmentSettingBinding
import com.mtg.tool.findmyphone.main.activity.Language2Activity
import com.mtg.tool.findmyphone.main.activity.PolicyWebViewActivity
import com.mtg.tool.findmyphone.utils.ActionUtils
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.LanguageUtils
import com.mtg.tool.findmyphone.utils.app.AppPreferences
import com.mtg.tool.findmyphone.utils.app.VibrateFlashThread
import com.mtg.tool.findmyphone.utils.hide

class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate) {
    private var appPreferences = AppPreferences.instance

    override fun initView() {
        setUpSelection()
        setUpRate()
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
            logEvent("click_set_flash_default")
            runVibrateFlashThread(MODE_FLASH_DEFAULT)
        }
        binding.rbFlashDisco.setOnClickListener {
            logEvent("click_set_flash_disco")
            runVibrateFlashThread(MODE_FLASH_DISCO)
        }
        binding.rbFlashSos.setOnClickListener {
            logEvent("click_set_flash_SOS")
            runVibrateFlashThread(MODE_FLASH_SOS)

        }
        binding.rbVibrateDefault.setOnClickListener {
            logEvent("click_set_vibrate_default")
            runVibrateFlashThread(MODE_VIBRATE_DEFAULT)

        }
        binding.rbVibrateStrong.setOnClickListener {
            logEvent("click_set_vibrate_strong")
            runVibrateFlashThread(MODE_VIBRATE_STRONG)
        }
        binding.rbVibrateHeart.setOnClickListener {
            logEvent("click_set_vibrate_heartbeat")
            runVibrateFlashThread(MODE_VIBRATE_HEART)
        }
        binding.rbVibrateTicktock.setOnClickListener {
            logEvent("click_set_vibrate_ticktock")
            runVibrateFlashThread(MODE_VIBRATE_TICKTOCK)

        }
        binding.llFlashController.setOnClickListener {
            binding.sbFlash.isChecked = !binding.sbFlash.isChecked
        }
        binding.sbFlash.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                logEvent("click_set_flash_off")
                lockFlash()
            } else {
                logEvent("click_set_flash_on")
                unlockFlash()
            }
        }

        binding.llVibrateController.setOnClickListener {
            binding.sbVibrate.isChecked = !binding.sbVibrate.isChecked
        }
        binding.sbVibrate.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                logEvent("click_set_vibrate_off")
                lockVibrate()
            } else {
                logEvent("click_set_vibrate_on")
                unlockVibrate()
            }
        }
        binding.sbSound.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                logEvent("click_set_sound_off")
            } else {
                logEvent("click_set_sound_on")
            }
        }

        binding.llSoundController.setOnClickListener {
            binding.sbSound.isChecked = !binding.sbVibrate.isChecked
        }
        binding.vLockFlash.setOnClickListener { Log.e("android_log_error", "it is disabled") }
        binding.vLockVibrate.setOnClickListener { Log.e("android_log_error", "it is disabled") }

        binding.btnLanguage.setOnClickListener {
            EventLogger.getInstance()?.logEvent("click_set_language")
            startActivity(Intent(requireActivity(), Language2Activity::class.java))
        }
        binding.btnRate.setOnClickListener {
            EventLogger.getInstance()?.logEvent("click_set_rate")
            ActionUtils.showRateDialog(requireActivity(), false, callback = {
                if (it) hideRate()
            })
        }
        binding.btnShare.setOnClickListener {
            EventLogger.getInstance()?.logEvent("click_set_share")
            ActionUtils.shareApp(requireActivity())
        }
        binding.btnFeedback.setOnClickListener {
            ActionUtils.sendFeedback(requireActivity())
        }
        binding.btnPrivacy.setOnClickListener {
            PolicyWebViewActivity.start(requireActivity())
        }
    }

    private fun runVibrateFlashThread(mode: Int) {
        if (VibrateFlashThread.isCancellable) {
            VibrateFlashThread(
                requireContext(),
                mode
            ).start()
        } else {
            when (mode) {
                MODE_FLASH_DEFAULT, MODE_FLASH_DISCO, MODE_FLASH_SOS -> {
                    appPreferences.currentFlash = mode
                }

                MODE_VIBRATE_DEFAULT, MODE_VIBRATE_STRONG, MODE_VIBRATE_HEART, MODE_VIBRATE_TICKTOCK -> {
                    appPreferences.currentVibrate = mode
                }
            }
        }
    }

    private fun unlockFlash() {
        binding.rgFlash.alpha = 1F
        binding.vLockFlash.visibility = View.GONE
    }

    private fun lockFlash() {
        if (VibrateFlashThread.isCancellable) {
            VibrateFlashThread.stopFlash()
        }
        binding.rgFlash.alpha = 0.3F
        binding.vLockFlash.visibility = View.VISIBLE
    }

    private fun unlockVibrate() {
        binding.rgVibrate.alpha = 1F
        binding.vLockVibrate.visibility = View.GONE
    }

    private fun lockVibrate() {
        if (VibrateFlashThread.isCancellable) {
            VibrateFlashThread.stopVibrate()
        }
        binding.rgVibrate.alpha = 0.3F
        binding.vLockVibrate.visibility = View.VISIBLE
    }


    private fun hideRate() {
        binding.btnRate.hide()
    }

    private fun setUpRate() {
        if (SharedPrefs.isRated(requireActivity())) {
            hideRate()
        }
    }

    override fun onPause() {
        super.onPause()
        appPreferences.hasSound = binding.sbSound.isChecked
        appPreferences.hasFlash = binding.sbFlash.isChecked
        appPreferences.hasVibrate = binding.sbVibrate.isChecked
    }

    override fun onResume() {
        super.onResume()
        binding.ivFlag.setImageResource(LanguageUtils.listCountryDefault[appPreferences.currentIndexLanguage].imageFlag)
    }
}