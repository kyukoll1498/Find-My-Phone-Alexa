package com.mtg.tool.findmyphone.main.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.mtg.tool.findmyphone.ACTION_NOTIFICATION_CLICKED_SERVICE
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.REQUEST_MICRO_PERMISSION_CODE
import com.mtg.tool.findmyphone.REQUEST_NOTIFICATION_PERMISSION_CODE
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.base.ViewPagerAddFragmentsAdapter
import com.mtg.tool.findmyphone.data.preferences.SharedPrefs
import com.mtg.tool.findmyphone.databinding.ActivityMainBinding
import com.mtg.tool.findmyphone.main.clap.VocalService
import com.mtg.tool.findmyphone.main.dialog.NotificationPermissionDialog
import com.mtg.tool.findmyphone.main.dialog.RecordPermissionDialog
import com.mtg.tool.findmyphone.main.fragment.AddFragment
import com.mtg.tool.findmyphone.main.fragment.HomeFragment
import com.mtg.tool.findmyphone.main.fragment.SettingFragment
import com.mtg.tool.findmyphone.main.fragment.SoundFragment
import com.mtg.tool.findmyphone.utils.ActionUtils
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.LanguageUtils
import com.mtg.tool.findmyphone.utils.PermissionUtils
import com.mtg.tool.findmyphone.utils.hide

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun binding() {
        isFullScreen = false
        super.binding()
    }

    override fun initView() {
        changeStatusBar(ContextCompat.getColor(this, R.color._138EFF))
//        setUpRate()
        setupViewpager()
//        setupDrawerNavigation()
        AdmobManager.getInstance().loadBanner(this, BuildConfig.banner_home, binding.frAd)
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(this, binding.frAd)
    }

    private var homeFragment = HomeFragment()

//    private fun setUpRate() {
//        if (SharedPrefs.isRated(this)) {
//            hideRate()
//        }
//    }

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
            logEvent("click_add")
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
            EventLogger.getInstance()?.logEvent("click_main_setting")
        }
        binding.btnHowToUse.setOnClickListener {
            logEvent("click_guide")
            startActivity(Intent(this, HowToUseActivity::class.java))
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


//    private fun setupDrawerNavigation() {
//        binding.navContent.tvVersion.text = "Ver ${BuildConfig.VERSION_NAME}"
//
//        binding.navContent.imgFlag.setImageResource(LanguageUtils.getFlagResourceID(this))
//
//        binding.navContent.btnLanguage.setOnClickListener {
//            EventLogger.getInstance()?.logEvent("click_set_language")
//            startActivity(Intent(this, LanguageActivity::class.java))
//        }
//        binding.navContent.btnRateNavigation.setOnClickListener {
//            EventLogger.getInstance()?.logEvent("click_set_rate")
//            ActionUtils.showRateDialog(this, false, callback = {
//                if (it) hideRate()
//            })
//        }
//        binding.navContent.btnShare.setOnClickListener {
//            EventLogger.getInstance()?.logEvent("click_set_share")
//            ActionUtils.shareApp(this)
//        }
//        binding.navContent.btnFeedback.setOnClickListener {
//            ActionUtils.sendFeedback(this)
//        }
//        binding.navContent.btnPrivacy.setOnClickListener {
//            PolicyWebViewActivity.start(this)
//        }
//    }
//
//    private fun hideRate() {
//        binding.navContent.btnRateNavigation.hide()
//    }

    private fun setupViewpager() {
        binding.viewpagerMain.apply {
            adapter = ViewPagerAddFragmentsAdapter(supportFragmentManager, lifecycle).apply {
                addFrag(homeFragment)
                addFrag(SoundFragment())
                addFrag(AddFragment())
                addFrag(SettingFragment())
            }
        }
        binding.viewpagerMain.offscreenPageLimit = 2
        binding.viewpagerMain.isUserInputEnabled = false
        binding.viewpagerMain.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.ivHome.isSelected = position == 0
                binding.ivSound.isSelected = position == 1
                binding.ivAdd.isSelected = position == 2
                binding.ivSetting.isSelected = position == 3

            }
        })
    }



    override fun onResume() {
        super.onResume()
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
//                    hideRate()
                }
            })
        } else {
            super.onBackPressed()
        }
    }
}