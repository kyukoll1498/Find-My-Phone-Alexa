package com.mtg.tool.findmyphone.main.activity

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.common.control.interfaces.AdCallback
import com.common.control.manager.AdmobManager
import com.common.control.utils.InternetUtil
import com.google.android.gms.ads.nativead.NativeAd
import com.mtg.tool.findmyphone.AdCache
import com.mtg.tool.findmyphone.AdIds
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.base.ViewPagerAddFragmentsAdapter
import com.mtg.tool.findmyphone.consent_dialog.remote_config.RemoteConfigManager
import com.mtg.tool.findmyphone.databinding.ActivityOnboardingBinding
import com.mtg.tool.findmyphone.main.fragment.OnBoardFragment
import com.mtg.tool.findmyphone.utils.EventLogger

class OnBoardActivity :
    BaseActivity<ActivityOnboardingBinding>(ActivityOnboardingBinding::inflate) {
    private var currentPageShowed: Int = 0
    private val onb4NativeAds = arrayListOf(AdIds.ob4_native_high2, AdIds.ob4_native)
    private val onb5NativeAds = arrayListOf(AdIds.ob5_native_high, AdIds.ob5_native)
    private val onb6NativeAds = arrayListOf(AdIds.ob6_native_high, AdIds.ob6_native)

    private var isHandleAds2 = false
    private var isHandleAds3 = false
    private var isHandleAds4 = false
    private var isHandleAds5 = false

    override fun binding() {
//        isFullScreen = true
        super.binding()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, OnBoardActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initView() {
        initViewPager()
    }

//    override fun onResume() {
//        super.onResume()
//        val adapter = ViewPagerAddFragmentsAdapter(
//            mContext, supportFragmentManager, lifecycle
//        )
//        binding.viewpagerOnboard.adapter =
//            adapter.apply {
//                addFrag(OnBoardFragment(0, R.drawable.img_inside_1, R.string.text_on_boarding_1))
//                addFrag(OnBoardFragment(1, R.drawable.img_inside_2, R.string.text_on_boarding_2))
//                addFrag(OnBoardFragment(2, R.drawable.img_inside_df_fullscreen, R.string.text_on_boarding_df_fullscreen))
//                addFrag(OnBoardFragment(3, R.drawable.img_inside_3, R.string.text_on_boarding_3))
//            }
//
//        binding.viewpagerOnboard.registerOnPageChangeCallback(object :
//            ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                currentPageShowed = position
//
//                binding.indicatorView.selection = position
//
//                if (position == 0 || position == adapter.itemCount - 1) {
//                    binding.indicatorView.visibility = View.VISIBLE
//                    binding.tvNext.visibility = View.VISIBLE
//                }
//                if (position == 1) {
//                    binding.indicatorView.visibility = View.VISIBLE
//                    binding.tvNext.visibility = View.VISIBLE
//                }
//                if (position == 2) {
//                    if (!RemoteConfigManager.instance!!.isShowNativeFullScreenOnboard
//                        || !InternetUtil.isNetworkAvailable(this@OnBoardActivity)
//                    ) {
//                        binding.indicatorView.visibility = View.VISIBLE
//                        binding.tvNext.visibility = View.VISIBLE
//                    } else{
//                        binding.indicatorView.visibility = View.GONE
//                        binding.tvNext.visibility = View.GONE
//                    }
//                }
//                if (position == 3) {
//                    binding.indicatorView.visibility = View.VISIBLE
//                    binding.tvNext.visibility = View.VISIBLE
//                }
//                if (position == (binding.viewpagerOnboard.adapter as ViewPagerAddFragmentsAdapter).itemCount - 1) {
//                    binding.tvNext.text = getString(R.string.get_started)
//                } else
//                    binding.tvNext.text = getString(R.string.next)
//            }
//        })
//
//        binding.tvNext.setOnClickListener {
//            logEvent("onboard" + (binding.viewpagerOnboard.currentItem + 2) + "_next_click")
//            if (binding.viewpagerOnboard.currentItem == (binding.viewpagerOnboard.adapter as ViewPagerAddFragmentsAdapter).itemCount - 1) {
//                startActivity(Intent(this@OnBoardActivity, PermissionActivity::class.java))
//                finish()
//            } else {
//                binding.viewpagerOnboard.currentItem++
//            }
//            when (binding.viewpagerOnboard.currentItem) {
//                0 -> EventLogger.getInstance()?.logEvent("click_guide_1")
//                1 -> EventLogger.getInstance()?.logEvent("click_guide_2")
//                2 -> EventLogger.getInstance()?.logEvent("click_guide_3")
//            }
//        }
//
//        binding.viewpagerOnboard.setCurrentItem(currentPageShowed, false)
//    }

    private fun initViewPager() {
        val adapter = ViewPagerAddFragmentsAdapter(
            mContext, supportFragmentManager, lifecycle
        )
        binding.viewpagerOnboard.adapter =
            adapter.apply {
                addFrag(OnBoardFragment(0, R.drawable.img_inside_1, R.string.text_on_boarding_1))
                addFrag(OnBoardFragment(1, R.drawable.img_inside_2, R.string.text_on_boarding_2))
                addFrag(OnBoardFragment(2, R.drawable.img_inside_df_fullscreen, R.string.text_on_boarding_df_fullscreen))
                addFrag(OnBoardFragment(3, R.drawable.img_inside_3, R.string.text_on_boarding_3))
            }

        binding.viewpagerOnboard.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.indicatorView.selection = position

                when (position) {
                    0 -> handleAds2()
                    1 -> handleAds3()
                    2 -> handleAds4()
                    3 -> handleAds5()
                }
                if (position == 0 || position == adapter.itemCount - 1) {
                    binding.indicatorView.visibility = View.VISIBLE
                    binding.tvNext.visibility = View.VISIBLE
                }
                if (position == 1) {
                    binding.indicatorView.visibility = View.VISIBLE
                    binding.tvNext.visibility = View.VISIBLE
                }
                if (position == 2) {
                    if (!RemoteConfigManager.instance!!.isShowNativeFullScreenOnboard
                        || !InternetUtil.isNetworkAvailable(this@OnBoardActivity)
                    ) {
                        binding.indicatorView.visibility = View.VISIBLE
                        binding.tvNext.visibility = View.VISIBLE
                    } else{
                        binding.indicatorView.visibility = View.GONE
                        binding.tvNext.visibility = View.GONE
                    }
                }
                if (position == 3) {
                    binding.indicatorView.visibility = View.VISIBLE
                    binding.tvNext.visibility = View.VISIBLE
                }
                if (position == (binding.viewpagerOnboard.adapter as ViewPagerAddFragmentsAdapter).itemCount - 1) {
                    binding.tvNext.text = getString(R.string.get_started)
                } else
                    binding.tvNext.text = getString(R.string.next)
            }
        })

        binding.tvNext.setOnClickListener {
            logEvent("onboard" + (binding.viewpagerOnboard.currentItem + 2) + "_next_click")
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

    fun handleAds2() {
        if (!isHandleAds2) {
            isHandleAds2 = true
            if (AdCache.getInstance().ob4NativeHigh == null && AdCache.getInstance().ob4NativeHigh1 == null) {
                if (InternetUtil.isNetworkAvailable(this)) {
                    AdmobManager.getInstance()
                        .preloadFullScreenAlternateNative(
                            this,
                            onb4NativeAds,
                            object : AdCallback() {
                                override fun onNativeAds(nativeAd: NativeAd?) {
                                    super.onNativeAds(nativeAd)
                                    AdCache.getInstance().ob4NativeHigh2AndNative =
                                        nativeAd
                                }

                                override fun onAdImpression() {
                                    super.onAdImpression()
                                    logEvent("onboard4_native_view")
                                }

                                override fun onAdClicked() {
                                    super.onAdClicked()
                                    logEvent("onboard4_native_click")

                                }
                            })
                }
            }
            if (InternetUtil.isNetworkAvailable(this)) {
                AdmobManager.getInstance()
                    .preloadAlternateNative(this, onb5NativeAds, object : AdCallback() {
                        override fun onNativeAds(nativeAd: NativeAd?) {
                            super.onNativeAds(nativeAd)
                            AdCache.getInstance().ob5NativeHigh.value =
                                nativeAd
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                            logEvent("onboard5_native_view")
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            logEvent("onboard5_native_click")

                        }
                    })
            }
        }
    }

    fun handleAds3() {
        if (!isHandleAds3) {
            if (InternetUtil.isNetworkAvailable(this)) {
                AdmobManager.getInstance()
                    .preloadAlternateNative(this, onb6NativeAds, object : AdCallback() {
                        override fun onNativeAds(nativeAd: NativeAd?) {
                            super.onNativeAds(nativeAd)
                            AdCache.getInstance().ob6NativeHigh.value =
                                nativeAd
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                            logEvent("onboard6_native_view")
                        }

                        override fun onAdClicked() {
                            super.onAdClicked()
                            logEvent("onboard6_native_click")

                        }
                    })
            }
            isHandleAds3 = true
        }
    }

    fun handleAds4() {

    }

    fun handleAds5() {
    }
}