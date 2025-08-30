package com.alx.findphone.claptofind.flashalert.main.fragment

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.alx.findphone.claptofind.flashalert.AVERAGE_SENSITIVITY
import com.alx.findphone.claptofind.flashalert.HIGH_SENSITIVITY
import com.alx.findphone.claptofind.flashalert.LOW_SENSITIVITY
import com.alx.findphone.claptofind.flashalert.MODE_FLASH_DEFAULT
import com.alx.findphone.claptofind.flashalert.MODE_FLASH_DISCO
import com.alx.findphone.claptofind.flashalert.MODE_FLASH_SOS
import com.alx.findphone.claptofind.flashalert.MODE_VIBRATE_DEFAULT
import com.alx.findphone.claptofind.flashalert.MODE_VIBRATE_HEART
import com.alx.findphone.claptofind.flashalert.MODE_VIBRATE_STRONG
import com.alx.findphone.claptofind.flashalert.MODE_VIBRATE_TICKTOCK
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.data.preferences.SharedPrefs
import com.alx.findphone.claptofind.flashalert.databinding.FragmentSettingBinding
import com.alx.findphone.claptofind.flashalert.main.activity.Language2Activity
import com.alx.findphone.claptofind.flashalert.main.activity.PolicyWebViewActivity
import com.alx.findphone.claptofind.flashalert.main.dialog.TutorialDialog
import com.alx.findphone.claptofind.flashalert.utils.ActionUtils
import com.alx.findphone.claptofind.flashalert.utils.EventLogger
import com.alx.findphone.claptofind.flashalert.utils.app.AppPreferences
import com.alx.findphone.claptofind.flashalert.utils.app.VibrateFlashThread
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.alx.findphone.claptofind.flashalert.utils.hide

class SettingFragment : BaseActivity<FragmentSettingBinding>(FragmentSettingBinding::inflate) {
    private var appPreferences = AppPreferences.Companion.instance

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, SettingFragment::class.java)
            context.startActivity(starter)
        }

        fun startFromUninstall(context: Context) {
            val starter = Intent(context, SettingFragment::class.java)
            starter.action = Constants.ACTION_CLICKED_SENSITIVITY_UNINSTALL
            context.startActivity(starter)
        }
    }

    override fun initView() {
        setUpSelection()
        setUpRate()

        if (intent.action.equals(Constants.ACTION_CLICKED_SENSITIVITY_UNINSTALL)) {
            TutorialDialog.start(this)
        }
    }

    private fun setUpSelection() {
        when (appPreferences.currentFlash) {
            MODE_FLASH_DEFAULT -> {
                binding?.rbFlashDefault?.isChecked = true
            }

            MODE_FLASH_DISCO -> {
                binding?.rbFlashDisco?.isChecked = true
            }

            MODE_FLASH_SOS -> {
                binding?.rbFlashSos?.isChecked = true
            }
        }

        when (appPreferences.currentVibrate) {
            MODE_VIBRATE_DEFAULT -> {
                binding?.rbVibrateDefault?.isChecked = true
            }

            MODE_VIBRATE_STRONG -> {
                binding?.rbVibrateStrong?.isChecked = true
            }

            MODE_VIBRATE_HEART -> {
                binding?.rbVibrateHeart?.isChecked = true
            }

            MODE_VIBRATE_TICKTOCK -> {
                binding?.rbVibrateTicktock?.isChecked = true
            }
        }

        when (appPreferences.currentSoundSensitivity) {
            HIGH_SENSITIVITY -> {
                binding?.rbHighSensitivity?.isChecked = true
            }

            AVERAGE_SENSITIVITY -> {
                binding?.rbAverageSensitivity?.isChecked = true
            }

            LOW_SENSITIVITY -> {
                binding?.rbLowSensitivity?.isChecked = true
            }
        }

        binding?.sbSound?.isChecked = appPreferences.hasSound
        if (appPreferences.hasFlash) {
            binding?.sbFlash?.isChecked = true
            unlockFlash()
        } else {
            binding?.sbFlash?.isChecked = false
            lockFlash()
        }
        if (appPreferences.hasVibrate) {
            binding?.sbVibrate?.isChecked = true
            unlockVibrate()
        } else {
            binding?.sbVibrate?.isChecked = false
            lockVibrate()
        }
    }

    override fun addEvent() {
        binding?.apply {
            rbFlashDefault.setOnClickListener {
                logEvent("click_set_flash_default")
                runVibrateFlashThread(MODE_FLASH_DEFAULT)
            }
            rbFlashDisco.setOnClickListener {
                logEvent("click_set_flash_disco")
                runVibrateFlashThread(MODE_FLASH_DISCO)
            }
            rbFlashSos.setOnClickListener {
                logEvent("click_set_flash_SOS")
                runVibrateFlashThread(MODE_FLASH_SOS)

            }
            rbVibrateDefault.setOnClickListener {
                logEvent("click_set_vibrate_default")
                runVibrateFlashThread(MODE_VIBRATE_DEFAULT)

            }
            rbVibrateStrong.setOnClickListener {
                logEvent("click_set_vibrate_strong")
                runVibrateFlashThread(MODE_VIBRATE_STRONG)
            }
            rbVibrateHeart.setOnClickListener {
                logEvent("click_set_vibrate_heartbeat")
                runVibrateFlashThread(MODE_VIBRATE_HEART)
            }
            rbVibrateTicktock.setOnClickListener {
                logEvent("click_set_vibrate_ticktock")
                runVibrateFlashThread(MODE_VIBRATE_TICKTOCK)

            }
            rbHighSensitivity.setOnClickListener {
                logEvent("click_set_sensitivity_high")
                appPreferences.currentSoundSensitivity = HIGH_SENSITIVITY
            }
            rbAverageSensitivity.setOnClickListener {
                logEvent("click_set_sensitivity_average")
                appPreferences.currentSoundSensitivity = AVERAGE_SENSITIVITY
            }
            rbLowSensitivity.setOnClickListener {
                logEvent("click_set_sensitivity_low")
                appPreferences.currentSoundSensitivity = LOW_SENSITIVITY
            }
            llFlashController.setOnClickListener {
                sbFlash.isChecked = !sbFlash.isChecked
            }
            sbFlash.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    logEvent("click_set_flash_off")
                    lockFlash()
                } else {
                    logEvent("click_set_flash_on")
                    unlockFlash()
                }
            }

            llVibrateController.setOnClickListener {
                sbVibrate.isChecked = !sbVibrate.isChecked
            }
            sbVibrate.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    logEvent("click_set_vibrate_off")
                    lockVibrate()
                } else {
                    logEvent("click_set_vibrate_on")
                    unlockVibrate()
                }
            }
            sbSound.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
                    logEvent("click_set_sound_off")
                } else {
                    logEvent("click_set_sound_on")
                }
            }

            llSoundController.setOnClickListener {
                sbSound.isChecked = !sbVibrate.isChecked
            }
            vLockFlash.setOnClickListener { Log.e("android_log_error", "it is disabled") }
            vLockVibrate.setOnClickListener { Log.e("android_log_error", "it is disabled") }

            btnLanguage.setOnClickListener {
                EventLogger.Companion.getInstance()?.logEvent("click_set_language")
                startActivity(Intent(this@SettingFragment, Language2Activity::class.java))
            }
            btnRate.setOnClickListener {
                EventLogger.Companion.getInstance()?.logEvent("click_set_rate")
                ActionUtils.showRateDialog(this@SettingFragment, false, callback = {
                    if (it) hideRate()
                })
            }
            btnShare.setOnClickListener {
                EventLogger.Companion.getInstance()?.logEvent("click_set_share")
                ActionUtils.shareApp(this@SettingFragment)
            }
            btnFeedback.setOnClickListener {
                ActionUtils.sendFeedback(this@SettingFragment)
            }
            btnPrivacy.setOnClickListener {
                PolicyWebViewActivity.Companion.start(this@SettingFragment)
            }
            ivBack.setOnClickListener {
                finish()
            }
            ivTutorial.setOnClickListener {
                TutorialDialog.start(this@SettingFragment)
            }
        }
    }

    private fun runVibrateFlashThread(mode: Int) {
        if (VibrateFlashThread.Companion.isCancellable) {
            VibrateFlashThread(
                this@SettingFragment, mode
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
        binding?.rgFlash?.alpha = 1F
        binding?.vLockFlash?.visibility = View.GONE
    }

    private fun lockFlash() {
        if (VibrateFlashThread.Companion.isCancellable) {
            VibrateFlashThread.Companion.stopFlash()
        }
        binding?.rgFlash?.alpha = 0.3F
        binding?.vLockFlash?.visibility = View.VISIBLE
    }

    private fun unlockVibrate() {
        binding?.rgVibrate?.alpha = 1F
        binding?.vLockVibrate?.visibility = View.GONE
    }

    private fun lockVibrate() {
        if (VibrateFlashThread.Companion.isCancellable) {
            VibrateFlashThread.Companion.stopVibrate()
        }
        binding?.rgVibrate?.alpha = 0.3F
        binding?.vLockVibrate?.visibility = View.VISIBLE
    }


    public fun hideRate() {
        binding?.btnRate?.hide()
    }

    private fun setUpRate() {
        if (SharedPrefs.isRated(this)) {
            hideRate()
        }
    }

    override fun onPause() {
        super.onPause()
        binding?.apply {
            appPreferences.hasSound = sbSound.isChecked
            appPreferences.hasFlash = sbFlash.isChecked
            appPreferences.hasVibrate = sbVibrate.isChecked
        }
    }

    override fun onResume() {
        super.onResume()
//        binding?.ivFlag?.setImageResource(LanguageUtils.listCountryDefault[appPreferences.currentIndexLanguage].imageFlag)
    }
}