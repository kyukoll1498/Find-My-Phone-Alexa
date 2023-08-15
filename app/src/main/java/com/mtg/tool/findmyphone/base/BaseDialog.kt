package com.mtg.tool.findmyphone.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import androidx.viewbinding.ViewBinding


open class BaseDialog<B : ViewBinding>(
    context: Context,
    val bindingFactory: (LayoutInflater) -> B
) : Dialog(context) {
    val binding: B by lazy { bindingFactory(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window!!.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(binding.root)
    }
}