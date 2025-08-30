package com.alx.findphone.claptofind.flashalert.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.data.preferences.SharedPrefs
import com.alx.findphone.claptofind.flashalert.main.activity.PolicyWebViewActivity
import com.alx.findphone.claptofind.flashalert.main.dialog.CustomRateAppDialog
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.common.control.interfaces.RateCallback
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

object ActionUtils {

    fun openLink(c: Context, url: String) {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url.replace("HTTPS", "https"))
            c.startActivity(i)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(c, "No browser!", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendFeedback(context: Context) {
        val selectorIntent = Intent(Intent.ACTION_SENDTO)
        selectorIntent.data = Uri.parse("mailto:")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(Constants.EMAIL))
        emailIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            context.getString(R.string.app_name) + " Feedback"
        )
        emailIntent.putExtra(Intent.EXTRA_TEXT, "")
        emailIntent.selector = selectorIntent
//        context.startActivity(Intent.createChooser(emailIntent, "Send email..."))
        try {
            context.startActivity(Intent.createChooser(emailIntent, "Send email using..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, "No email clients installed.", Toast.LENGTH_SHORT).show()
        }
    }

    fun showRateDialog(context: Activity, isFinish: Boolean, callback: (Boolean) -> Unit) {
        CustomRateAppDialog.start(context, object : RateCallback {
            override fun onMaybeLater() {
                if (isFinish) {
                    SharedPrefs.increaseCountRate(context)
                    context.finishAffinity()
                }
            }

            override fun onSubmit(review: String) {
                callback(true)
                SharedPrefs.setRated(context)
                if (isFinish) {
                    context.finishAffinity()
                }

            }

            override fun onRate() {
                rateInApp(context)
                SharedPrefs.setRated(context)
                callback(true)

            }

            override fun starRate(v: Float) {
                if (isFinish) {
                    context.finishAffinity()
                }
            }
        })

//        val dialog = CustomRateAppDialog(context)
//        dialog.setCallback(object : RateCallback {
//            override fun onMaybeLater() {
//                if (isFinish) {
//                    SharedPrefs.increaseCountRate(context)
//                    context.finishAffinity()
//                }
//            }
//
//            override fun onSubmit(review: String) {
//                callback(true)
//                SharedPrefs.setRated(context)
//                if (isFinish) {
//                    context.finishAffinity()
//                }
//
//            }
//
//            override fun onRate() {
//                rateInApp(context)
//                SharedPrefs.setRated(context)
//                callback(true)
//
//            }
//
//            override fun starRate(v: Float) {
//                if (isFinish) {
//                    context.finishAffinity()
//                }
//            }
//        })
//        dialog.show()
    }

    private fun rateInApp(context: Activity) {
        val manager: ReviewManager = ReviewManagerFactory.create(context)
        val request: Task<ReviewInfo> = manager.requestReviewFlow()
        request.addOnCompleteListener(OnCompleteListener<ReviewInfo?> { task: Task<ReviewInfo?> ->
            if (task.isSuccessful) {
                // We can get the ReviewInfo object
                val reviewInfo = task.result
                reviewInfo?.let { manager.launchReviewFlow(context, it) }
            }
        })
    }

    fun shareApp(context: Context) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = "https://play.google.com/store/apps/details?id=" + context.packageName
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, Constants.SUBJECT)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        context.startActivity(Intent.createChooser(sharingIntent, "Share to"))
    }

    fun showPolicy(context: Context) {
        try {
            PolicyWebViewActivity.Companion.start(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}