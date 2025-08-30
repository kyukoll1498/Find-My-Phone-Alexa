package com.alx.findphone.claptofind.flashalert.main.activity

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.alx.findphone.claptofind.flashalert.ACTION_NOTIFICATION_CLICKED_SERVICE
import com.alx.findphone.claptofind.flashalert.AdIds
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.REQUEST_MICRO_PERMISSION_CODE
import com.alx.findphone.claptofind.flashalert.REQUEST_NOTIFICATION_PERMISSION_CODE
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.base.ViewPagerAddFragmentsMainAdapter
import com.alx.findphone.claptofind.flashalert.consent_dialog.remote_config.RemoteConfigManager
import com.alx.findphone.claptofind.flashalert.data.preferences.SharedPrefs
import com.alx.findphone.claptofind.flashalert.databinding.ActivityMainBinding
import com.alx.findphone.claptofind.flashalert.main.dialog.ExitDialog
import com.alx.findphone.claptofind.flashalert.main.dialog.NotificationPermissionDialog
import com.alx.findphone.claptofind.flashalert.main.dialog.RecordPermissionDialog
import com.alx.findphone.claptofind.flashalert.main.fragment.HomeFragment
import com.alx.findphone.claptofind.flashalert.main.fragment.SettingFragment
import com.alx.findphone.claptofind.flashalert.utils.ActionUtils
import com.alx.findphone.claptofind.flashalert.utils.EventLogger
import com.alx.findphone.claptofind.flashalert.utils.PermissionUtils
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.facebook.appevents.AppEventsLogger
import java.util.Timer
import java.util.TimerTask

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private val logger by lazy {
        AppEventsLogger.newLogger(this)
    }

    override fun binding() {
        isFullScreen = false
        super.binding()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }

        fun startFromUninstall(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.action = Constants.ACTION_CLICKED_SENSITIVITY_UNINSTALL
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }

    private var canRefreshBanner = true
    private val listBanner by lazy { arrayListOf(AdIds.banner_home_high, AdIds.banner_home) }

    override fun initView() {
        logSentFriendRequestEvent()
        changeStatusBar(Color.parseColor("#e7f4ff"))
//        setUpRate()
        setupViewpager()
//        setupDrawerNavigation()
        loadAlternateBanner()

        if (intent.action.equals(Constants.ACTION_CLICKED_SENSITIVITY_UNINSTALL)) {
            SettingFragment.startFromUninstall(this)
        }
    }

    private fun loadAlternateBanner() {
        AdmobManager.getInstance().loadAlternateBanner(
            this, listBanner, binding.frAd,
            object : AdCallback() {
                override fun onAdImpression() {
                    super.onAdImpression()
                    logEvent("home_banner_view")
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    logEvent("home_banner_click")
                }
            }
        )
        val timeLoad = RemoteConfigManager.Companion.instance!!.time_load_banner
        if (timeLoad != 0.toLong()) {
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        if (canRefreshBanner) {
                            reloadBanner()
                        }
                    }

                }
            }, timeLoad * 1000, timeLoad * 1000)
        }
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(this, binding.frAd)
    }

    private fun logSentFriendRequestEvent() {
        logger.logEvent("sentFriendRequest")
    }

    private fun reloadBanner() {
        if (listBanner.isNotEmpty()) {
            Log.d("AdmobRefresh: ", "home" + listBanner[0])
            AdmobManager.getInstance()
                .loadAlternateBanner(
                    this, arrayListOf(listBanner[0]), binding.frAd,
                    object : AdCallback() {
                        override fun onAdImpression() {
                            super.onAdImpression()
                            logEvent("home_banner_view")
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            logEvent("home_banner_click")
                        }
                    }
                )
        }
    }

    private var homeFragment = HomeFragment()
    private var settingFragment = SettingFragment()

    override fun addEvent() {
        binding.btnHome.setOnClickListener {
            logEvent("click_home")
            changeUITools(binding.viewpagerMain.currentItem, 0)
            binding.viewpagerMain.setCurrentItem(0, false)
        }
        binding.btnSound.setOnClickListener {
            logEvent("click_sound")
            changeUITools(binding.viewpagerMain.currentItem, 1)
            binding.viewpagerMain.setCurrentItem(1, false)
        }
        binding.btnAdd.setOnClickListener {
            logEvent("add_click")
            changeUITools(binding.viewpagerMain.currentItem, 2)
            binding.viewpagerMain.setCurrentItem(2, false)
        }
        binding.btnSetting.setOnClickListener {
            logEvent("click_setting")
            changeUITools(binding.viewpagerMain.currentItem, 3)
            binding.viewpagerMain.setCurrentItem(3, false)
        }
        binding.btnDrawer.setOnClickListener {
            logEvent("click_menu")
            binding.drawerLayout.openDrawer(GravityCompat.START)
            EventLogger.Companion.getInstance()?.logEvent("click_main_setting")
        }
        binding.btnHowToUse.setOnClickListener {
            binding.btnHowToUse.isEnabled = false
            Log.d("<><>", "Inactive")
            when (binding.viewpagerMain.currentItem) {
                0 -> logEvent("home_tutorial_click")
                1 -> logEvent("sound_tutorial_click")
                3 -> logEvent("add_tutorial_click")
            }

            startActivity(Intent(this, HowToUseActivity::class.java))

            binding.btnHowToUse.postDelayed({
                binding.btnHowToUse.isEnabled = true
                Log.d("<><>", "Aactive")
            }, 200)
        }
        binding.btnDrawer.setOnClickListener {
            logEvent("click_setting")
            startActivity(Intent(this, SettingActivity::class.java))
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvAppName.setSelected(true)
    }

    private fun changeUITools(currentItem: Int, nextItem: Int) {
        if (currentItem != nextItem) {
            when (currentItem) {
                0 -> {
                    binding.ivHome.setImageDrawable(getDrawable(R.drawable.ic_home))
                    binding.tvHome.setTextColor(ColorStateList.valueOf(Color.parseColor("#868686")))
                }

                1 -> {
                    binding.ivSound.setImageDrawable(getDrawable(R.drawable.ic_sound))
                    binding.tvSound.setTextColor(ColorStateList.valueOf(Color.parseColor("#868686")))
                }

                2 -> {
                    binding.ivAdd.setImageDrawable(getDrawable(R.drawable.ic_import))
                    binding.tvAdd.setTextColor(ColorStateList.valueOf(Color.parseColor("#868686")))
                }

                3 -> {
                    binding.ivSetting.setImageDrawable(getDrawable(R.drawable.ic_settings))
                    binding.tvSetting.setTextColor(ColorStateList.valueOf(Color.parseColor("#868686")))
                }
            }
            when (nextItem) {
                0 -> {
                    binding.ivHome.setImageDrawable(getDrawable(R.drawable.ic_home_selected))
                    binding.tvHome.setTextColor(ColorStateList.valueOf(Color.parseColor("#1380FF")))
                }

                1 -> {
                    binding.ivSound.setImageDrawable(getDrawable(R.drawable.ic_sound_selected))
                    binding.tvSound.setTextColor(ColorStateList.valueOf(Color.parseColor("#1380FF")))
                }

                2 -> {
                    binding.ivAdd.setImageDrawable(getDrawable(R.drawable.ic_import_selected))
                    binding.tvAdd.setTextColor(ColorStateList.valueOf(Color.parseColor("#1380FF")))
                }

                3 -> {
                    binding.ivSetting.setImageDrawable(getDrawable(R.drawable.ic_settings_selected))
                    binding.tvSetting.setTextColor(ColorStateList.valueOf(Color.parseColor("#1380FF")))
                }
            }
        }
    }

    private fun setupViewpager() {
        binding.viewpagerMain.apply {
            adapter =
                ViewPagerAddFragmentsMainAdapter(context, supportFragmentManager, lifecycle).apply {
//                addFrag(homeFragment)
//                addFrag(SoundFragment())
//                addFrag(AddFragment())
//                addFrag(settingFragment)
                }
        }
//        binding.viewpagerMain.offscreenPageLimit = 2
        binding.viewpagerMain.isUserInputEnabled = false
        binding.viewpagerMain.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> logEvent("home_view")
                    1 -> logEvent("sound_view")
                    2 -> logEvent("add_view")
                    3 -> logEvent("setting_view")
                }
                binding.ivHome.isSelected = position == 0
                binding.ivSound.isSelected = position == 1
                binding.ivAdd.isSelected = position == 2
                binding.ivSetting.isSelected = position == 3

            }
        })
    }


    override fun onResume() {
        super.onResume()
        canRefreshBanner = true
        changeUITools(0, binding.viewpagerMain.currentItem)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == ACTION_NOTIFICATION_CLICKED_SERVICE) {
            logEvent("click_noti_deactivate")
            homeFragment.turnOffDetective()
            changeUITools(binding.viewpagerMain.currentItem, 0)
            binding.viewpagerMain.setCurrentItem(0, false)
            binding.ivHome.isSelected = false
        } else {
            val viewPagerPosition = intent?.getIntExtra("VIEWPAGER_POSITION", 0)
            if (viewPagerPosition != null) {
                changeUITools(binding.viewpagerMain.currentItem, 0)
                binding.viewpagerMain.setCurrentItem(viewPagerPosition, false)
            }
            binding.ivHome.isSelected = viewPagerPosition == 0
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_MICRO_PERMISSION_CODE) {
            if (!PermissionUtils.checkMicroPermission(this)) {
                showRecordPermissionDialog()
                return
            }
        }
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION_CODE) {
            if (!PermissionUtils.checkNotificationPermission(this)) {
                showNotificationPermissionDialog()
                return
            }
        }
    }

    private fun showRecordPermissionDialog() {
        RecordPermissionDialog(this) {
            if (it) {
                PermissionUtils.goSettingsForMicroPermission(this)
            }
        }.show()
    }

    private fun showNotificationPermissionDialog() {
        NotificationPermissionDialog(this) {
            if (it) {
                PermissionUtils.goSettingsForNotificationPermission(this)
            }
        }.show()
    }


    override fun onBackPressed() {
        if (!SharedPrefs.isRated(this)) {
            ActionUtils.showRateDialog(this, true, callback = {
                if (it) {
                    settingFragment.hideRate()
                }
            })
        } else {
            ExitDialog(this) {
                super.onBackPressed()
            }.show()
        }
    }

    override fun onPause() {
        super.onPause()
        canRefreshBanner = false
    }
}