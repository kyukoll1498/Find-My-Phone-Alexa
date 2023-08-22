package com.mtg.tool.findmyphone.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics


open class BaseDialog<B : ViewBinding>(
    context: Context,
    val bindingFactory: (LayoutInflater) -> B
) : Dialog(context) {
    val binding: B by lazy { bindingFactory(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window!!.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)
    }
    open fun logEvent(value: String) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(context)
        try {
            Log.d("android_log", "logEvent: $value")
            val bundle = Bundle()
            bundle.putString("EVENT", value)
            firebaseAnalytics.logEvent(value, bundle)
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }
}