package com.alx.findphone.claptofind.flashalert.main.fragment

import android.annotation.SuppressLint
import android.app.Activity
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
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.recyclerview.widget.GridLayoutManager
import com.alx.findphone.claptofind.flashalert.ACTION_FINISH_DETECT
import com.alx.findphone.claptofind.flashalert.ACTION_NOTIFICATION_CLICKED_SERVICE
import com.alx.findphone.claptofind.flashalert.ADAPTER_ADS_TYPE
import com.alx.findphone.claptofind.flashalert.ADAPTER_ITEM_TYPE
import com.alx.findphone.claptofind.flashalert.AdIds
import com.alx.findphone.claptofind.flashalert.KEY_SOUND
import com.alx.findphone.claptofind.flashalert.KEY_SOUND_ITEM_DATA
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.data.model.SoundItem
import com.alx.findphone.claptofind.flashalert.data.repo.AppRepository
import com.alx.findphone.claptofind.flashalert.databinding.FragmentHomeBinding
import com.alx.findphone.claptofind.flashalert.main.activity.MainActivity
import com.alx.findphone.claptofind.flashalert.main.activity.PlaySoundActivity
import com.alx.findphone.claptofind.flashalert.main.activity.ScreenAlertActivity
import com.alx.findphone.claptofind.flashalert.main.activity.UninstallActivity
import com.alx.findphone.claptofind.flashalert.main.adapter.SoundAdapter
import com.alx.findphone.claptofind.flashalert.main.clap.ClassesApp
import com.common.control.base.OnActionCallback
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.common.control.utils.BroadcastUtils
import com.google.android.gms.ads.nativead.NativeAd
import com.alx.findphone.claptofind.flashalert.main.clap.FeatureClapManager
import com.alx.findphone.claptofind.flashalert.main.clap.VocalService
import com.alx.findphone.claptofind.flashalert.utils.PermissionUtils
import com.alx.findphone.claptofind.flashalert.utils.app.AppPreferences
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants


open class HomeFragment : BaseActivity<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    private var classesApp: ClassesApp? = null
    private lateinit var currentSoundItem: SoundItem
    private var intOnTick = 0
    private var mPermCAm: Boolean? = null
    private var isCircleActiveVisible = false
    private var showTxtContent = true
    private var isClapActive = false
    private var isFirstLoad = true
    private val nativeAds by lazy {
        arrayListOf(AdIds.native_home_high, AdIds.native_home)
    }
    private lateinit var soundAdapter: SoundAdapter

    companion object {
        @JvmStatic
        fun newIntent(context: Activity): Intent {
            val starter = Intent(context, HomeFragment::class.java)
            starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            return starter
        }

        @JvmStatic
        fun start(context: Activity) {
            val starter = newIntent(context)
            context.startActivity(starter)
        }
    }

    private val finishDetectReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context, intent: Intent) {
            turnOffDetective()
        }
    }

    private val handleTurnOnClap: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            binding.clClickTab.performClick()
        }
    }

    override fun initView() {
        BroadcastUtils.registerReceiver(
            this, finishDetectReceiver, IntentFilter(ACTION_FINISH_DETECT)
        )
        BroadcastUtils.registerReceiver(
            this, handleTurnOnClap, IntentFilter(Constants.APPLY_ACTIVE_CLAP)
        )
        val data = intent.getStringExtra(Constants.key_shortcut)
        when (data) {
            Constants.shortcut_uninstall -> {
                UninstallActivity.Companion.start(this)
            }
        }
        firstLoad()
        val visible = View.VISIBLE
        val invisible = View.INVISIBLE
        BroadcastUtils.registerReceiver(
            this, finishDetectReceiver, IntentFilter(ACTION_FINISH_DETECT)
        )
        if (isMyServiceRunning()) {
            isCircleActiveVisible = !isCircleActiveVisible
            binding?.apply {
                ivCircleActive.visibility = visible
                txtActive.visibility = visible
                txtInactive.visibility = invisible
                if (showTxtContent) {
//                    llTvInactive.visibility = visible
//                    tvInactive.visibility = invisible
                }
                lavClickInactive.visibility = invisible
                lavClickActive.visibility = visible
            }
        }
        if (intent?.action == ACTION_NOTIFICATION_CLICKED_SERVICE) {
            turnOffDetective()
        }

        classesApp = ClassesApp(this)
//        context?.stopService(Intent(activity, VocalService::class.java))
        window?.addFlags(128)
        settingSound()
        setupList()
    }

    private fun setupList() {
        soundAdapter = SoundAdapter(AppRepository.getSoundMain(this), this, isFirstLoad)
        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (soundAdapter.getItemViewType(position)) {
                    ADAPTER_ADS_TYPE -> 3
                    ADAPTER_ITEM_TYPE -> 1
                    else -> -1
                }
            }
        }
        soundAdapter.mCallback = OnActionCallback { key, data ->
            if (key == KEY_SOUND) {
                val soundItem = data[0] as SoundItem
                logEvent("click_detail_play_" + soundItem.name?.replace(" ", "_")?.lowercase())
                val intent = Intent(this, PlaySoundActivity::class.java)
                intent.putExtra(KEY_SOUND_ITEM_DATA, soundItem)
                startActivity(intent)
            }
        }
        binding.rcvSound.layoutManager = gridLayoutManager
        binding.rcvSound.adapter = soundAdapter
    }

    private fun firstLoad() {
        if (isFirstLoad) {
            loadAlternateNative()
            Log.d("Hoho Refresh", "Hoho Init")
        }
    }

    private fun loadAlternateNative() {
        AdmobManager.getInstance().preloadAlternateNative(
            this@HomeFragment, nativeAds, object : AdCallback() {
                override fun onNativeAds(nativeAd: NativeAd?) {
                    super.onNativeAds(nativeAd)
                    AdmobManager.getInstance().showNative(
                        this@HomeFragment, nativeAd, binding?.frAd, AdmobManager.NativeAdType.SMALL
                    )
                    Log.e("HomeFragment", "Fragment not attached to context")
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                    logEvent("home_native_view")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    logEvent("home_native_click")
                }
            })
    }


    override fun onResume() {
        super.onResume()
        setUpWidthData()
        if (!AppOpenManager.getInstance().isShowingAd) {
            if (!isFirstLoad) {
                loadAlternateNative()
                Log.d("Hoho Refresh", "Hoho Resume")
            }
        }
        isFirstLoad = false
        Handler().postDelayed(Runnable {
            try {
                setUpResponsive()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 500)

        val currentSound = AppPreferences.Companion.instance.currentSound
        val sounds = AppRepository.getSoundMain(this)
        for (item in sounds) {
            item.isSelected = (item.soundPath == currentSound.soundPath)
        }
        soundAdapter.submitList(sounds)
    }

    private fun setUpResponsive() {
        binding?.apply {
            if (txtActive.y + txtActive.height > tvInactive.y) {
//                tvInactive.visibility = View.GONE
//            binding.txtInactive.visibility = View.GONE
//            binding.txtActive.visibility = View.GONE
//                llTvInactive.visibility = View.GONE
                showTxtContent = false
            }
        }
    }

    private fun setUpWidthData() {
        currentSoundItem = AppPreferences.Companion.instance.currentSound
//        currentSoundItem.let {
//            if (it.avatar == R.drawable.ic_default_audio_avatar) {
//                binding?.let { it1 ->
//                    Glide.with(this).load(R.drawable.ic_main_default_audio).into(
//                        it1.ivAvatar
//                    )
//                }
//            } else {
//                binding?.let { it1 -> Glide.with(this).load(it.avatar).into(it1.ivAvatar) }
//            }
//        }
//        binding?.tvName?.text = AppRepository.getAllSound(this)
//            .find { it.soundPath == currentSoundItem.soundPath }?.name ?: currentSoundItem.name
    }

    override fun addEvent() {
        initializePlayerAndStartRecording()
        checkCamFlash()
        binding.llMoreSound.setOnClickListener {
            SoundFragment.start(this)
        }
        binding.llCreateSound.setOnClickListener {
            AddFragment.start(this)
        }
        binding.ivSettings.setOnClickListener {
            SettingFragment.start(this)
            logEvent("home_setting_click")
        }
        binding.tvScAlert.setOnClickListener {
            ScreenAlertActivity.Companion.start(this)
        }
    }

//    override fun loadAds() {
//        super.loadAds()
//        AppOpenManager.getInstance()
//            .hideNativeOrBannerWhenShowOpenApp(context as Activity?, binding.frAd)
//    }


    private fun initVolume() {
        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager?
        audioManager!!.getStreamVolume(3)
        audioManager.setStreamVolume(
            3, (audioManager.getStreamMaxVolume(3).toFloat() * (classesApp!!.read(NotificationCompat.CATEGORY_PROGRESS, "50")!!.toFloat() / 100.0f)).toInt(), 0
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
        for (runningServiceInfo in (getSystemService(ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
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
        if (!PermissionUtils.checkMicroPermission(this)) {
            PermissionUtils.requestMicroPermission(this)
        } else {
            Log.d("Error Permission Micro", "Check Permission")
        }
    }

    private fun checkPermissionNotification() {
        if (!PermissionUtils.checkNotificationPermission(this)) {
            PermissionUtils.requestNotificationPermission(this)
        } else {
            Log.d("Error Permission Notification", "Check Permission")
        }
    }

    private fun initializePlayerAndStartRecording() {
        val visible = View.VISIBLE
        val invisible = View.INVISIBLE
        binding?.clClickTab?.setOnClickListener {
            checkPermissionMicro()
            if (PermissionUtils.checkMicroPermission(this)) {
                checkPermissionNotification()
            }
            if (PermissionUtils.checkMicroPermission(this) && PermissionUtils.checkNotificationPermission(this)) {
                if (!isCircleActiveVisible) {
                    logEvent("home_activate_click")
                    isCircleActiveVisible = !isCircleActiveVisible
                    binding?.apply {
                        ivCircleActive.visibility = visible
                        txtActive.visibility = visible
                        txtInactive.visibility = invisible
                        if (showTxtContent) {
//                            llTvInactive.visibility = visible
//                            tvInactive.visibility = invisible
                        }
                        lavClickInactive.visibility = invisible
                        lavClickActive.visibility = visible
                        classesApp!!.save("StopService", "0")
                        this@HomeFragment.let {
                            startForegroundService(
                                it, Intent(this@HomeFragment, VocalService::class.java)
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
        binding?.apply {
            ivCircleActive.visibility = invisible
            txtActive.visibility = invisible
            txtInactive.visibility = visible
            if (showTxtContent) {
//                tvInactive.visibility = visible
//                llTvInactive.visibility = invisible
            }

            lavClickInactive.visibility = visible
            lavClickInactive.resumeAnimation()
            lavClickActive.visibility = invisible
            FeatureClapManager.getInstance(this@HomeFragment).apply {
                stopAll()
            }
            stopService(Intent(this@HomeFragment, VocalService::class.java))
//            Toast.makeText(requireContext(), "Detection stopped", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(finishDetectReceiver)
    }
}