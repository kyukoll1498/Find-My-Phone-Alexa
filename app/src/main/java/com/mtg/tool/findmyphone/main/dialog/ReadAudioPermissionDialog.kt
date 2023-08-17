package com.mtg.tool.findmyphone.main.dialog

import android.content.Context
import android.os.Bundle
import com.mtg.tool.findmyphone.base.BaseDialog
import com.mtg.tool.findmyphone.databinding.DialogReadAudioPermissionBinding
import com.mtg.tool.findmyphone.databinding.DialogRecordPermissionBinding

open class ReadAudioPermissionDialog(context: Context, val callback: (Boolean) -> Unit) :
    BaseDialog<DialogReadAudioPermissionBinding>(context, DialogReadAudioPermissionBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addEvent()
    }

    fun setDialogCancellable(flag: Boolean): ReadAudioPermissionDialog {
        setCancelable(flag)
        return this
    }
    private fun addEvent() {
        binding.btnDeny.setOnClickListener {
            callback.invoke(false)
            dismiss()
        }
        binding.btnAllow.setOnClickListener {
            callback.invoke(true)
            dismiss()
        }
    }
}