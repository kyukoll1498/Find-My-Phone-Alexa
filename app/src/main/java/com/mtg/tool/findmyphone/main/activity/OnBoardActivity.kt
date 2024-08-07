package com.mtg.tool.findmyphone.main.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import androidx.viewpager2.widget.ViewPager2
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.base.ViewPagerAddFragmentsAdapter
import com.mtg.tool.findmyphone.data.preferences.SharedPrefs
import com.mtg.tool.findmyphone.databinding.ActivityOnboardingBinding
import com.mtg.tool.findmyphone.main.fragment.OnBoardFragment
import com.mtg.tool.findmyphone.utils.EventLogger
import com.mtg.tool.findmyphone.utils.constant.Constants
import com.mtg.tool.findmyphone.utils.setSize

class OnBoardActivity :
    BaseActivity<ActivityOnboardingBinding>(ActivityOnboardingBinding::inflate) {
    override fun binding() {
        isFullScreen = true
        super.binding()
    }

    companion object{
        fun start(context: Context){
            val intent = Intent(context, OnBoardActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initView() {
        initViewPager()

        AdmobManager.getInstance().loadNative(
            this,
            BuildConfig.native_guide,
            binding.frAd,
            R.layout.custom_native_onboarding
        )
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(this, binding.frAd)

    }

    private fun initViewPager() {
        binding.viewpagerOnboard.adapter =
            ViewPagerAddFragmentsAdapter(supportFragmentManager, lifecycle).apply {
                addFrag(OnBoardFragment(R.drawable.img_inside_1, R.string.text_on_boarding_1))
                addFrag(OnBoardFragment(R.drawable.img_inside_2, R.string.text_on_boarding_2))
                addFrag(OnBoardFragment(R.drawable.img_inside_3, R.string.text_on_boarding_3))
            }
        binding.viewpagerOnboard.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.indicatorView.selection = position
                if (position == (binding.viewpagerOnboard.adapter as ViewPagerAddFragmentsAdapter).itemCount - 1) {
                    binding.tvNext.text = getString(R.string.get_started)
                } else
                    binding.tvNext.text = getString(R.string.next)
            }
        })

        binding.tvNext.setOnClickListener {
            if (binding.viewpagerOnboard.currentItem == (binding.viewpagerOnboard.adapter as ViewPagerAddFragmentsAdapter).itemCount - 1) {
                startActivity(Intent(this@OnBoardActivity, PermissionActivity::class.java))
                finish()
            } else {
                binding.viewpagerOnboard.currentItem++
            }
            when (binding.viewpagerOnboard.currentItem) {
                0 -> EventLogger.getInstance()?.logEvent("click_guide_1")
                1 -> EventLogger.getInstance()?.logEvent("click_guide_2")
                2 -> EventLogger.getInstance()?.logEvent("click_guide_3")
            }
        }
    }

    override fun addEvent() {

    }
}