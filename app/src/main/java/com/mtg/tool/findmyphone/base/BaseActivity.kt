package com.mtg.tool.findmyphone.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.viewbinding.ViewBinding
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.mtg.tool.findmyphone.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


abstract class BaseActivity<B : ViewBinding>(val bindingFactory: (LayoutInflater) -> B) :
    LocalizationActivity(), CoroutineScope {
    val TAG = "~~~"
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    private lateinit var job: Job

    private var firebaseAnalytics: FirebaseAnalytics? = null

    /**
     * set isFullscreen
     */
    var isFullScreen = false

    val binding: B by lazy { bindingFactory(layoutInflater) }

    lateinit var mContext: Context

    open fun binding() {
        if (isFullScreen)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        changeStatusBar(ContextCompat.getColor(this, R.color.white))
        setContentView(binding.root)
    }

    override fun onResume() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
        super.onResume()
    }

    open fun loadAds() {}

    /**
     * to set size of view (TextView,..etc) by screen width
     */
    abstract fun initView()

    protected abstract fun addEvent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        mContext = this
        binding()
        initView()
        loadAds()
        addEvent()

        //firebaseAnalytics = FirebaseAnalytics.getInstance(this)


        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    protected open fun changeStatusBar(color: String?) {
        window.statusBarColor = Color.parseColor(color)
    }

    protected open fun changeStatusBar(color: Int) {
        window.statusBarColor = color
    }

    /**
     * Log event to firebase
     */
    open fun logEvent(value: String) {
        if (firebaseAnalytics == null) {
            return
        }
        try {
            Log.d("android_log", "logEvent: $value")
            val bundle = Bundle()
            bundle.putString("EVENT", value)
            firebaseAnalytics!!.logEvent(value, bundle)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        Handler().postDelayed({
            try {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 600)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

}