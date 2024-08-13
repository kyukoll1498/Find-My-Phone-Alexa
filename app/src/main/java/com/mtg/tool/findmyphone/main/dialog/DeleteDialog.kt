package com.mtg.tool.findmyphone.main.dialog

import android.content.Context
import android.os.Bundle
import android.text.Html
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseDialog
import com.mtg.tool.findmyphone.databinding.DialogDeleteBinding

open class DeleteDialog(context: Context, val name: String, val callback: (Boolean) -> Unit) :
    BaseDialog<DialogDeleteBinding>(context, DialogDeleteBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tvContent.text =
            Html.fromHtml(context.getString(R.string.are_you_sure_you_want_to_delete))
        addEvent()
    }

    private fun addEvent() {
        binding.tvCancel.setOnClickListener {
            dismiss()
        }
        binding.tvDelete.setOnClickListener {
            callback.invoke(true)
            dismiss()
        }
    }
}