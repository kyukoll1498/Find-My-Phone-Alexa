package com.mtg.tool.findmyphone.main.fragment

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
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
import com.mtg.tool.findmyphone.main.clap.DetectClapClap
import com.mtg.tool.findmyphone.main.clap.VocalService


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate){
    private var classesApp: ClassesApp? = null
    private var intOnTick = 0
    var mPermCAm: Boolean? = null
    private var MainIsRun = false
    var mySong: MediaPlayer? = null
    private val clapDetector: DetectClapClap? = null


    fun MainActivity() {}
    private var isCircleActiveVisible = false

    override fun initView() {
        changeColorText()

        isMyServiceRunning()

        if (isMyServiceRunning()) {
            binding.clClickTab.visibility = View.VISIBLE
            binding.clClickTab.visibility = View.VISIBLE
        }
        classesApp = ClassesApp(requireContext())


        classesApp!!.save("detectClap", "1")
        context?.stopService(Intent(activity, VocalService::class.java))
        setVolume(classesApp!!.read("seekBar", "50")!!.toInt())
        activity?.getWindow()?.addFlags(128)

        settingSound()
        checkCamFlash()
//        check()
    }

    override fun addEvent() {
        clickClap()
        binding.clClickTab.setOnClickListener {
            checkPermissionMicro()
            if (isDetectionEnabled) {
                activity?.stopService(Intent(context, VocalService::class.java))
                binding.tvInactive.visibility = View.INVISIBLE
                binding.clClickTab.visibility = View.INVISIBLE
                binding.clClickTab.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Detection stopped", Toast.LENGTH_LONG).show()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    requireActivity().startForegroundService(Intent(context, VocalService::class.java))
                } else
                    requireActivity().startService(Intent(context, VocalService::class.java))
                Toast.makeText(requireContext(), "Detection started", Toast.LENGTH_LONG).show()
            }
            isDetectionEnabled = !isDetectionEnabled
        }

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

    private fun clickClap() {
        val visible = View.VISIBLE
        val invisible = View.INVISIBLE
        binding.clClickTab.setOnClickListener {
            isCircleActiveVisible = !isCircleActiveVisible
            if (isCircleActiveVisible) {
                binding.apply {
                    ivCircleActive.visibility = visible
                    txtActive.visibility = visible
                    txtInactive.visibility = invisible
                    llTvInactive.visibility = visible
                    tvInactive.visibility = invisible
                }

            } else {
                binding.apply {
                    ivCircleActive.visibility = invisible
                    txtInactive.visibility = visible
                    txtActive.visibility = invisible
                    tvInactive.visibility = visible
                    llTvInactive.visibility = invisible
                }
            }
        }
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

    private fun setVolume(i: Int) {
        val audioManager = activity?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(3, (audioManager.getStreamMaxVolume(3).toFloat() * (i.toFloat() / 100.0f)).toInt(), 0)
    }

    private fun isMyServiceRunning(): Boolean {
        for (runningServiceInfo in (activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(Int.MAX_VALUE)) {
            if (VocalService::class.java.name == runningServiceInfo.service.className) {
                return true
            }
        }
        return false
    }


    fun onBackPressed() {
        val intent: Intent = Intent(requireContext(), HomeFragment::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
//        finish()
    }

    private var isDetectionEnabled = false

    private fun checkCamFlash() {
        if (ContextCompat.checkSelfPermission(requireContext(), "android.permission.CAMERA") != 0) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf("android.permission.CAMERA"), 223)
            return
        }
        mPermCAm = java.lang.Boolean.TRUE
    }

    private fun checkPermissionMicro() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 123)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                initializePlayerAndStartRecording()
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(i: Int, strArr: Array<String?>, iArr: IntArray) {
        if (i == 123) {
            if (iArr.isNotEmpty() && iArr[0] == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    initializePlayerAndStartRecording()
                }
            }
        } else if (i == 223) {
            mPermCAm = if (iArr.isEmpty() || iArr[0] != 0) {
                java.lang.Boolean.FALSE
            } else {
                java.lang.Boolean.TRUE
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun initializePlayerAndStartRecording() {
        classesApp!!.save("StopService", "0")
        context?.let { startForegroundService(it, Intent(context, VocalService::class.java)) }
        Toast.makeText(requireContext(), "Detection started", Toast.LENGTH_LONG).show()
        if (binding.txtInactive.visibility == View.INVISIBLE) {
            binding.clClickTab.visibility = View.VISIBLE
        }
        if (binding.clClickTab.visibility == View.VISIBLE) {
            binding.clClickTab.visibility = View.INVISIBLE
            binding.clClickTab.visibility = View.VISIBLE
        }
    }
}