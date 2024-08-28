package com.mtg.tool.findmyphone.main.fragment

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startForegroundService
import com.bumptech.glide.Glide
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.common.control.utils.BroadcastUtils
import com.google.android.gms.ads.nativead.NativeAd
import com.mtg.tool.findmyphone.ACTION_FINISH_DETECT
import com.mtg.tool.findmyphone.ACTION_NOTIFICATION_CLICKED_SERVICE
import com.mtg.tool.findmyphone.AdIds
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.data.repo.AppRepository
import com.mtg.tool.findmyphone.databinding.FragmentHomeBinding
import com.mtg.tool.findmyphone.main.clap.ClassesApp
import com.mtg.tool.findmyphone.main.clap.FeatureClapManager
import com.mtg.tool.findmyphone.main.clap.VocalService
import com.mtg.tool.findmyphone.utils.PermissionUtils
import com.mtg.tool.findmyphone.utils.app.AppPreferences


@Suppress("DEPRECATION")
open class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private var classesApp: ClassesApp? = null
    private lateinit var currentSoundItem: SoundItem
    private var intOnTick = 0
    private var mPermCAm: Boolean? = null
    private var isCircleActiveVisible = false
    private var showTxtContent = true
    private var isClapActive = false
    private val nativeAds by lazy {
        arrayListOf(AdIds.native_home_high, AdIds.native_home)
    }

    private val finishDetectReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context, intent: Intent) {
            turnOffDetective()
        }
    }

    override fun initView() {
        val visible = View.VISIBLE
        val invisible = View.INVISIBLE
        BroadcastUtils.registerReceiver(
            context,
            finishDetectReceiver,
            IntentFilter(ACTION_FINISH_DETECT)
        )
        if (isMyServiceRunning()) {
            isCircleActiveVisible = !isCircleActiveVisible
            binding.apply {
                ivCircleActive.visibility = visible
                txtActive.visibility = visible
                txtInactive.visibility = invisible
                if (showTxtContent) {
                    llTvInactive.visibility = visible
                    tvInactive.visibility = invisible
                }
                lavClickInactive.visibility = invisible
                lavClickActive.visibility = visible
            }
        }
        if (activity?.intent?.action == ACTION_NOTIFICATION_CLICKED_SERVICE) {
            turnOffDetective()
        }

        classesApp = ClassesApp(requireContext())
//        context?.stopService(Intent(activity, VocalService::class.java))
        activity?.window?.addFlags(128)
        settingSound()
    }

    private fun loadAlternateNative(){
        Log.d("Refresh","Refresh")
        AdmobManager.getInstance().preloadAlternateNative(
            requireActivity(),
            nativeAds,
            object : AdCallback(){
                override fun onNativeAds(nativeAd: NativeAd?) {
                    super.onNativeAds(nativeAd)
                    AdmobManager.getInstance().showNative(requireContext(), nativeAd, binding.frAd, AdmobManager.NativeAdType.SMALL)
                }
                override fun onAdImpression() {
                    super.onAdImpression()
                    logEvent("home_native_view")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    logEvent("home_native_click")
                }
            }
        )
    }


    override fun onResume() {
        super.onResume()
        setUpWidthData()
        Handler().postDelayed(Runnable {
            try {
                setUpResponsive()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 500)
        if (!AppOpenManager.getInstance().isShowingAd){
            loadAlternateNative()
        }
    }

    private fun setUpResponsive() {
        if (binding.txtActive.y + binding.txtActive.height > binding.tvInactive.y) {
            binding.tvInactive.visibility = View.GONE
//            binding.txtInactive.visibility = View.GONE
//            binding.txtActive.visibility = View.GONE
            binding.llTvInactive.visibility = View.GONE
            showTxtContent = false
        }
    }

    private fun setUpWidthData() {
        currentSoundItem = AppPreferences.instance.currentSound
        currentSoundItem.let {
            if (it.avatar == R.drawable.ic_default_audio_avatar) {
                Glide.with(this).load(R.drawable.ic_main_default_audio).into(binding.ivAvatar)
            } else {
                Glide.with(this).load(it.avatar).into(binding.ivAvatar)
            }
        }
        binding.tvName.text = AppRepository.getAllSound(requireContext()).find { it.soundPath == currentSoundItem.soundPath }?.name?:currentSoundItem.name
    }

    override fun addEvent() {
        initializePlayerAndStartRecording()
        checkCamFlash()
    }

//    override fun loadAds() {
//        super.loadAds()
//        AppOpenManager.getInstance()
//            .hideNativeOrBannerWhenShowOpenApp(context as Activity?, binding.frAd)
//    }


    private fun initVolume() {
        val audioManager = activity?.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        audioManager!!.getStreamVolume(3)
        audioManager.setStreamVolume(
            3,
            (audioManager.getStreamMaxVolume(3)
                .toFloat() * (classesApp!!.read(NotificationCompat.CATEGORY_PROGRESS, "50")!!
                .toFloat() / 100.0f)).toInt(),
            0
        )
    }

    private fun settingSound() {
        initVolume()
        object : CountDownTimer(5000, 500) {
            override fun onTick(j: Long) {
                this@HomeFragment.intOnTick += 1
            }

            override fun onFinish() {
                this@HomeFragment.intOnTick = 0
            }
        }.start()
    }

    private fun isMyServiceRunning(): Boolean {
        for (runningServiceInfo in (activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
            Int.MAX_VALUE
        )) {
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
        } else {
            Log.d("Error Permission Micro", "Check Permission")
        }
    }

    private fun checkPermissionNotification() {
        if (!PermissionUtils.checkNotificationPermission(requireContext())) {
            PermissionUtils.requestNotificationPermission(requireActivity())
        } else {
            Log.d("Error Permission Notification", "Check Permission")
        }
    }

    private fun initializePlayerAndStartRecording() {
        val visible = View.VISIBLE
        val invisible = View.INVISIBLE
        binding.clClickTab.setOnClickListener {
            checkPermissionMicro()
            if (PermissionUtils.checkMicroPermission(requireContext())) {
                checkPermissionNotification()
            }
            if (PermissionUtils.checkMicroPermission(requireContext()) &&
                PermissionUtils.checkNotificationPermission(requireContext())
            ) {
                if (!isCircleActiveVisible) {
                    logEvent("home_activate_click")
                    isCircleActiveVisible = !isCircleActiveVisible
                    binding.apply {
                        ivCircleActive.visibility = visible
                        txtActive.visibility = visible
                        txtInactive.visibility = invisible
                        if (showTxtContent) {
                            llTvInactive.visibility = visible
                            tvInactive.visibility = invisible
                        }
                        lavClickInactive.visibility = invisible
                        lavClickActive.visibility = visible
                        classesApp!!.save("StopService", "0")
                        context?.let {
                            startForegroundService(
                                it,
                                Intent(context, VocalService::class.java)
                            )
                        }
//                        Toast.makeText(requireContext(), "Detection started", Toast.LENGTH_LONG).show()
                    }
                } else {
                    logEvent("home_deactivate_click")
                    turnOffDetective()
                }
            }
        }
    }

    open fun turnOffDetective() {
        isCircleActiveVisible = !isCircleActiveVisible
        val visible = View.VISIBLE
        val invisible = View.INVISIBLE
        binding.apply {
            ivCircleActive.visibility = invisible
            txtActive.visibility = invisible
            txtInactive.visibility = visible
            if (showTxtContent) {
                tvInactive.visibility = visible
                llTvInactive.visibility = invisible
            }

            lavClickInactive.visibility = visible
            lavClickInactive.resumeAnimation()
            lavClickActive.visibility = invisible
            FeatureClapManager.getInstance(requireContext()).apply {
                stopAll()
            }
            activity?.stopService(Intent(context, VocalService::class.java))
//            Toast.makeText(requireContext(), "Detection stopped", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().unregisterReceiver(finishDetectReceiver)
    }
}