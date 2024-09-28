package com.mtg.tool.findmyphone.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.analytics.FirebaseAnalytics


abstract class BaseFragment<B : ViewBinding>(val bindingFactory: (LayoutInflater) -> B) :
    Fragment() {

    private var _binding: B? = null
    val binding: B get() = _binding ?: throw IllegalStateException("Binding is not initialized")

    open fun loadAds() {}

    abstract fun initView()

    abstract fun addEvent()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = bindingFactory(layoutInflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        addEvent()
        loadAds()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    open fun logEvent(value: String) {
        val firebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
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
