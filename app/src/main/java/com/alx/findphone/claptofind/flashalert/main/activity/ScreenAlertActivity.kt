package com.alx.findphone.claptofind.flashalert.main.activity

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.alx.findphone.claptofind.flashalert.R
import com.alx.findphone.claptofind.flashalert.base.BaseActivity
import com.alx.findphone.claptofind.flashalert.data.model.ItemAlert
import com.alx.findphone.claptofind.flashalert.databinding.ActivityScreenAlertBinding
import com.alx.findphone.claptofind.flashalert.main.adapter.ScreenAlertAdapter
import com.alx.findphone.claptofind.flashalert.utils.app.AppPreferences
import com.alx.findphone.claptofind.flashalert.utils.constant.Constants
import com.common.control.base.OnActionCallback

class ScreenAlertActivity : BaseActivity<ActivityScreenAlertBinding>(ActivityScreenAlertBinding::inflate) {
    private var isClickClap: Boolean = false
    private lateinit var mItemAlert: ItemAlert

    companion object {
        @JvmStatic
        fun start(context: Context, isClickClap: Boolean = false) {
            val starter = Intent(context, ScreenAlertActivity::class.java)
            starter.putExtra(Constants.KEY_DATA, isClickClap)
            context.startActivity(starter)
        }
    }

    override fun initView() {
        isClickClap = intent.getBooleanExtra(Constants.KEY_DATA, false)

        val list = mutableListOf(
            ItemAlert(R.drawable.sc_alert1, R.drawable.sc_alert1, R.drawable.bt_turnoff1, R.color.color_212121, false),
            ItemAlert(R.drawable.sc_alert2, R.drawable.sc_alert2, R.drawable.bt_turnoff2, R.color.color_212121, false),
            ItemAlert(R.drawable.sc_alert3, R.drawable.sc_alert3, R.drawable.bt_turnoff3, R.color.white, false),
            ItemAlert(R.drawable.sc_alert4, R.drawable.sc_alert4, R.drawable.bt_turnoff4, R.color.white, false),
            ItemAlert(R.drawable.sc_alert5, R.drawable.sc_alert5, R.drawable.bt_turnoff5, R.color.color_212121, false),
            ItemAlert(R.drawable.sc_alert6, R.drawable.sc_alert6, R.drawable.bt_turnoff6, R.color.color_212121, false),
            ItemAlert(R.drawable.sc_alert7, R.drawable.sc_alert7, R.drawable.bt_turnoff7, R.color.white, false),
            ItemAlert(R.drawable.sc_alert8, R.drawable.sc_alert8, R.drawable.bt_turnoff8, R.color.color_212121, false),
            ItemAlert(R.drawable.sc_alert9, R.drawable.sc_alert9, R.drawable.bt_turnoff9, R.color.color_212121, false),
            ItemAlert(R.drawable.sc_alert10, R.drawable.sc_alert10, R.drawable.bt_turnoff10, R.color.white, false),
        )

        mItemAlert = AppPreferences.Companion.instance.getCurrentItemAlert()

        val adapter = ScreenAlertAdapter(list, this)
        adapter.mCallback = OnActionCallback { key, value ->
            if (key.equals(Constants.KEY_DATA)) {
                val itemAlert = value[0] as ItemAlert
//                mItemAlert = itemAlert
//                for (item in list) {
//                    item.isSelected = item == itemAlert
//                }
//                adapter.notifyDataSetChanged()
                AlertApplyActivity.start(this, itemAlert)
            }
        }
        for (item in list) {
            if (item.imageRaw == mItemAlert.imageRaw) {
                item.isSelected = true
                adapter.notifyDataSetChanged()
            }
        }
        binding.rcvAlert.adapter = adapter
        binding.rcvAlert.layoutManager = GridLayoutManager(this, 2)
    }

    override fun addEvent() {
        binding.ivBack.setOnClickListener {
            finish()
        }
//        binding.tvApply.setOnClickListener {
//            requestClick()
//            sendTrackAction(Analytics.AC_SCREEN_ALERT_APPLY)
//            if (!Settings.canDrawOverlays(this)) {
//                OverlayPermissionDialog.start(this, false)
//                return@setOnClickListener
//            }
//
//            if (!isClickClap) {
//                showToast(Context.getString(R.string.apply_success))
//            } else {
//                ContextWrapper.sendBroadcast(Intent(Constants.APPLY_ACTIVE_CLAP))
//            }
//            mItemAlert.isSelected = false
//            AppPrefsManager.get().setCurrentItemAlert(mItemAlert)
//            exitSelf(false)
//        }

        binding.ivTutorial.setOnClickListener {
            AlertTutorialActivity.start(this)
        }
    }
}