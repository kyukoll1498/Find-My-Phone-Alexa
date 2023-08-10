package com.mtg.tool.findmyphone.main.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.crashlytics.internal.common.CommonUtils
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.base.ViewPagerAddFragmentsAdapter
import com.mtg.tool.findmyphone.databinding.ActivityMainBinding
import com.mtg.tool.findmyphone.main.fragment.AddFragment
import com.mtg.tool.findmyphone.main.fragment.HomeFragment
import com.mtg.tool.findmyphone.main.fragment.SettingFragment
import com.mtg.tool.findmyphone.main.fragment.SoundFragment
import com.mtg.tool.findmyphone.utils.ActionUtils
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.LanguageUtils
import com.mtg.tool.findmyphone.utils.hide

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun initView() {
        setupViewpager()
        setupDrawerNavigation()
    }

    override fun addEvent() {
        binding.btnHome.setOnClickListener {
            binding.viewpagerMain.setCurrentItem(0, false)
        }
        binding.btnSound.setOnClickListener {
            binding.viewpagerMain.setCurrentItem(1, false)
        }
        binding.btnAdd.setOnClickListener {
            binding.viewpagerMain.setCurrentItem(2, false)
        }
        binding.btnSetting.setOnClickListener {
            binding.viewpagerMain.setCurrentItem(3, false)
        }
        binding.btnDrawer.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
            EventLogger.getInstance()?.logEvent("click_main_setting")
        }

    }

    private fun setupDrawerNavigation() {
        binding.navContent.imgFlag.setImageResource(LanguageUtils.getFlagResourceID(this))

        binding.navContent.btnLanguage.setOnClickListener {
            EventLogger.getInstance()?.logEvent("click_set_language")
            startActivity(Intent(this, LanguageActivity::class.java))
        }
        binding.navContent.btnRateNavigation.setOnClickListener {
            EventLogger.getInstance()?.logEvent("click_set_rate")
            ActionUtils.showRateDialog(this, false, callback = {
                if (it) hideRate()
            })
        }
        binding.navContent.btnShare.setOnClickListener {
            EventLogger.getInstance()?.logEvent("click_set_share")
            ActionUtils.shareApp(this)
        }
        binding.navContent.btnFeedback.setOnClickListener {
            ActionUtils.sendFeedback(this)
        }
        binding.navContent.btnPrivacy.setOnClickListener {
            ActionUtils.showPolicy(this)
        }
    }
    private fun hideRate() {
        binding.btnRate.hide()
        binding.navContent.btnRateNavigation.hide()
    }
    private fun setupViewpager() {
        binding.viewpagerMain.apply {
            adapter = ViewPagerAddFragmentsAdapter(supportFragmentManager, lifecycle).apply {
                addFrag(HomeFragment())
                addFrag(SoundFragment())
                addFrag(AddFragment())
                addFrag(SettingFragment())
            }
        }
        binding.viewpagerMain.offscreenPageLimit = 2
        binding.viewpagerMain.isUserInputEnabled = false
        binding.viewpagerMain.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.imgHome.isSelected = position == 0
                binding.imgSound.isSelected = position == 1
                binding.imgAdd.isSelected = position == 2
                binding.imgSetting.isSelected = position == 3

            }
        })
    }
}