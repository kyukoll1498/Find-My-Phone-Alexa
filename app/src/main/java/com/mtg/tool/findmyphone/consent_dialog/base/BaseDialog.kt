package com.mtg.tool.findmyphone.consent_dialog.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.common.control.base.OnActionCallback
import com.mtg.tool.findmyphone.base.BaseActivity

abstract class BaseDialog<T : ViewDataBinding?> : DialogFragment() {
    @JvmField
    protected var binding: T? = null
    @JvmField
    protected var callback: OnActionCallback? = null
    var dismissListener: OnActionCallback? = null
    fun setCallback(callback: OnActionCallback?) {
        this.callback = callback
    }

    fun showDialog(activity: BaseActivity<*>) {
        show(activity.supportFragmentManager, null)
    }

    fun showDialog(activity: AppCompatActivity) {
        show(activity.supportFragmentManager, null)
    }

    fun showDialog(activity: AppCompatActivity, callback: OnActionCallback?) {
        setCallback(callback)
        show(activity.supportFragmentManager, null)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (dialog != null && dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        initView()
        addEvent()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dismissListener != null) {
            dismissListener!!.callback(null, null)
        }
    }

    protected abstract val layoutId: Int
    protected abstract fun initView()
    protected abstract fun addEvent()
}