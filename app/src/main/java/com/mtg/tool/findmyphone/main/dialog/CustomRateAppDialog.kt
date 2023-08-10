package com.mtg.tool.findmyphone.main.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.EditText
import com.common.control.interfaces.RateCallback
import com.mtg.tool.findmyphone.R
import com.ymb.ratingbar_lib.RatingBar

class CustomRateAppDialog(context: Context?) : Dialog(context!!) {
    private var handler: Handler? = null
    private var callback: RateCallback? = null
    private var edtContent: EditText? = null
    private var rd: Runnable? = null
    fun setCallback(callback: RateCallback?) {
        this.callback = callback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_custom_rate)
    }

    override fun show() {
        super.show()
        initView()
    }

    private fun initView() {
        setCanceledOnTouchOutside(false)
        val rating = findViewById<RatingBar>(R.id.rating)
        edtContent = findViewById(R.id.edt_content)
        setOnDismissListener { dialogInterface: DialogInterface? -> callback!!.onDismiss() }
        findViewById<View>(R.id.tv_submit).setOnClickListener {
            dismiss()
            callback!!.onSubmit(edtContent?.getText().toString())
        }
        findViewById<View>(R.id.ln_later).setOnClickListener { v: View? ->
            dismiss()
            callback!!.onMaybeLater()
        }
        rating.setOnRatingChangedListener { v, v1 ->
            if (handler != null && rd != null) {
                handler!!.removeCallbacks(rd!!)
            }
            handler = Handler()
            rd = Runnable {
                if (v1 < 4.0) {
                    findViewById<View>(R.id.ln_feedback).visibility = View.VISIBLE
                    findViewById<View>(R.id.ln_later).visibility = View.GONE
                    return@Runnable
                }
                dismiss()
                callback!!.starRate(v1)
                callback!!.onRate()
            }
            handler!!.postDelayed(rd!!, 200)
        }
    }

    override fun onBackPressed() {}
}