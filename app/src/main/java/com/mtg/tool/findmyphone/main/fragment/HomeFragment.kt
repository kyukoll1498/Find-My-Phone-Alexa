package com.mtg.tool.findmyphone.main.fragment

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.databinding.FragmentHomeBinding
import com.mtg.tool.findmyphone.main.clap.ClassesApp
import com.mtg.tool.findmyphone.main.clap.VocalService
import com.mtg.tool.findmyphone.utils.PermissionUtils


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private var classesApp: ClassesApp? = null
    private var intOnTick = 0
    private var mPermCAm: Boolean? = null
    private var isCircleActiveVisible = false

    override fun initView() {
        changeColorText()
        isMyServiceRunning()
        classesApp = ClassesApp(requireContext())
        classesApp!!.save("detectClap", "1")
        context?.stopService(Intent(activity, VocalService::class.java))
        activity?.window?.addFlags(128)

        settingSound()
    }

    override fun addEvent() {
        initializePlayerAndStartRecording()
        checkCamFlash()
    }

    private fun changeColorText() {
        val activationText = getString(R.string.activation_text)
        val spannableString = SpannableString(activationText)
        val colorGreen = ContextCompat.getColor(requireContext(), R.color.txt_inactive2)
        spannableString.setSpan(ForegroundColorSpan(colorGreen), 29, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(colorGreen), 9, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvInactive.text = spannableString
    }

    override fun loadAds() {
        super.loadAds()
        AdmobManager.getInstance().loadNative(context, BuildConfig.native_language, binding.frAd, R.layout.custom_native_language)
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(context as Activity?, binding.frAd)
    }


    private fun initVolume() {
        val audioManager = activity?.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        audioManager!!.getStreamVolume(3)
        audioManager.setStreamVolume(3, (audioManager.getStreamMaxVolume(3).toFloat() * (classesApp!!.read(NotificationCompat.CATEGORY_PROGRESS, "50")!!.toFloat() / 100.0f)).toInt(), 0)
    }

    private fun settingSound() {
        initVolume()
        object : CountDownTimer(5000, 500) {
            override fun onTick(j: Long) {
                this@HomeFragment.intOnTick = this@HomeFragment.intOnTick + 1
            }

            override fun onFinish() {
                this@HomeFragment.intOnTick = 0
            }
        }.start()
    }

    private fun isMyServiceRunning(): Boolean {
        for (runningServiceInfo in (activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(Int.MAX_VALUE)) {
            if (VocalService::class.java.name == runningServiceInfo.service.className) {
                return true
            }
        }
        return false
    }

    private fun checkCamFlash() {
        mPermCAm = java.lang.Boolean.TRUE
    }

    private fun checkPermissionMicro() {
        if (!PermissionUtils.checkMicroPermission(requireContext())) {
            PermissionUtils.requestMicroPermission(requireActivity())
        }
    }

    private fun initializePlayerAndStartRecording() {
        val visible = View.VISIBLE
        val invisible = View.INVISIBLE
        binding.clClickTab.setOnClickListener {
            checkPermissionMicro()
            if (PermissionUtils.checkMicroPermission(requireContext())) {
                isCircleActiveVisible = !isCircleActiveVisible
                Log.e("android_log", isCircleActiveVisible.toString())
                if (isCircleActiveVisible) {

                    binding.apply {
                        ivCircleActive.visibility = visible
                        txtActive.visibility = visible
                        txtInactive.visibility = invisible
                        llTvInactive.visibility = visible
                        tvInactive.visibility = invisible
                        classesApp!!.save("StopService", "0")
                        context?.let { startForegroundService(it, Intent(context, VocalService::class.java)) }
                        Toast.makeText(requireContext(), "Detection started", Toast.LENGTH_LONG).show()
                    }

                } else {
                    binding.apply {
                        ivCircleActive.visibility = invisible
                        txtInactive.visibility = visible
                        txtActive.visibility = invisible
                        tvInactive.visibility = visible
                        llTvInactive.visibility = invisible
                        activity?.stopService(Intent(context, VocalService::class.java))
                        Toast.makeText(requireContext(), "Detection stopped", Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }
}