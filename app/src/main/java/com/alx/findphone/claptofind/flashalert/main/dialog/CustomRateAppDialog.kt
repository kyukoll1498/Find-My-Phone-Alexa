package com.alx.findphone.claptofind.flashalert.main.dialog

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.widget.EditText
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.data.model.Emoji
import com.alx.findphone.claptofind.flashalert.databinding.DialogCustomRateBinding
import com.bumptech.glide.Glide
import com.common.control.interfaces.RateCallback

class CustomRateAppDialog : BaseActivity<DialogCustomRateBinding>(
    DialogCustomRateBinding::inflate
) {
    private var handler: Handler? = null
    private var edtContent: EditText? = null
    private var rd: Runnable? = null
    private val list: MutableList<Emoji> = ArrayList<Emoji>()

    companion object {
        @JvmStatic
        private var callback: RateCallback? = null
        fun start(context: Context, callback: RateCallback) {
            this.callback = callback
            val starter = Intent(context, CustomRateAppDialog::class.java)
            context.startActivity(starter)
        }
    }

    override fun initView() {
        initListEmoji()
        binding.tvSubmit.setOnClickListener {
            finish()
            callback!!.onSubmit(edtContent?.getText().toString())
        }
        binding.lnLater.setOnClickListener {
            finish()
            callback!!.onMaybeLater()
        }
        binding.rating.setOnRatingChangedListener { v, v1 ->
            if (handler != null && rd != null) {
                handler!!.removeCallbacks(rd!!)
            }
            val rate = v1.toInt()
            handler = Handler(mainLooper)
            val emoji = list[rate]
            Glide.with(this).load(emoji.icon).into(binding.icon)
            binding.tvFirstContent.text = emoji.title
            binding.tvSecondContent.text = emoji.subTitle
            rd = Runnable {
                if (v1 < 4.0) {
//                    findViewById<View>(R.id.ln_feedback).visibility = View.VISIBLE
//                    findViewById<View>(R.id.ln_later).visibility = View.GONE
                    binding.tvRate.text = getString(R.string.rate)
                    return@Runnable
                }
                binding.tvRate.text = getString(R.string.rate_on_play)
//                dismiss()
                callback!!.starRate(v1)
                callback!!.onRate()
            }
            handler!!.postDelayed(rd!!, 200)
        }

        binding.tvRate.setOnClickListener {
            if (binding.rating.rating < 4.0) {
                showThanksForFeedback()
            } else {
                showThanksForFeedback()
                callback!!.onRate()
            }
            finish()
        }
    }

    override fun addEvent() {

    }

    private fun showThanksForFeedback() {
        DialogThanksFeedback.start(this)
    }

    private fun initListEmoji() {
        list.add(
            Emoji(
                R.drawable.ic_0, getString(R.string.title_rate0), getString(
                    R.string.content_rate0
                )
            )
        )
        list.add(
            Emoji(
                R.drawable.ic_1, getString(R.string.title_rate1), getString(R.string.content_rate1)
            )
        )
        list.add(
            Emoji(
                R.drawable.ic_2, getString(R.string.title_rate2), getString(R.string.content_rate2)
            )
        )
        list.add(
            Emoji(
                R.drawable.ic_3, getString(R.string.title_rate3), getString(R.string.content_rate3)
            )
        )
        list.add(
            Emoji(
                R.drawable.ic_4, getString(R.string.title_rate4), getString(R.string.content_rate4)
            )
        )
        list.add(
            Emoji(
                R.drawable.ic_5, getString(R.string.title_rate5), getString(R.string.content_rate5)
            )
        )
    }
}