package com.mtg.tool.findmyphone.main.activity

import android.content.Context
import android.content.Intent
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.consent_dialog.ConsentDialogManager
import com.mtg.tool.findmyphone.consent_dialog.base.EventLogger
import com.mtg.tool.findmyphone.databinding.ActivityPolicyWebviewBinding
import com.mtg.tool.findmyphone.utils.constant.Constants

class PolicyWebViewActivity :
    BaseActivity<ActivityPolicyWebviewBinding>(ActivityPolicyWebviewBinding::inflate) {

    override fun initView() {
        val web: WebView = findViewById(R.id.webView)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return if (url.contains("play.google.com")) {
                    EventLogger.firebaseLog(this@PolicyWebViewActivity, "gdpr_policy")
                    ConsentDialogManager.instance
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