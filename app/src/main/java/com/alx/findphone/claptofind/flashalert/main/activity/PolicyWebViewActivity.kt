package com.alx.findphone.claptofind.flashalert.main.activity

import android.content.Context
import android.content.Intent
import android.webkit.WebView
import android.webkit.WebViewClient
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.consent_dialog.ConsentDialogManager
import com.alx.findphone.claptofind.flashalert.consent_dialog.base.EventLogger
import com.alx.findphone.claptofind.flashalert.databinding.ActivityPolicyWebviewBinding
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants

class PolicyWebViewActivity :
    BaseActivity<ActivityPolicyWebviewBinding>(ActivityPolicyWebviewBinding::inflate) {

    override fun initView() {
        val web: WebView = findViewById(R.id.webView)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return if (url.contains("play.google.com")) {
                    EventLogger.firebaseLog(this@PolicyWebViewActivity, "gdpr_policy")
                    ConsentDialogManager.Companion.instance
                        ?.showConsentDialogWebView(this@PolicyWebViewActivity)
                    true
                } else {
                    false
                }
            }
        }
        Constants.POLICY_URL.let { web.loadUrl(it) }
    }

    override fun addEvent() {}

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, PolicyWebViewActivity::class.java)
            context.startActivity(starter)
        }
    }
}