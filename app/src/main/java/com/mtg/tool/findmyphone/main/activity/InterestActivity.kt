package com.mtg.tool.findmyphone.main.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.widget.Button
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.preferences.SharedPrefs
import com.mtg.tool.findmyphone.databinding.ActivityInterestBinding
import com.mtg.tool.findmyphone.utils.constant.Constants
import com.mtg.tool.findmyphone.utils.setSize

class InterestActivity :
    BaseActivity<ActivityInterestBinding>(ActivityInterestBinding::inflate) {
    private lateinit var selectedInterests: MutableList<String>

    override fun binding() {
        isFullScreen = false
        SharedPrefs.put(this, Constants.SKIP_ONBOARD, true)
        super.binding()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, InterestActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun initView() {
        selectedInterests = mutableListOf()

        // Initialize buttons
        val buttons = listOf(
            binding.btnHome,
            binding.btnBedroom,
            binding.btnSofa,
            binding.btnWork,
            binding.btnGym,
            binding.btnFriendHouse,
            binding.btnCafe,
            binding.btnOutdoors,
            binding.btnSchool,
            binding.btnCar,
            binding.btnHotel,
            binding.btnEvents,
            binding.btnShoppingMall,
            binding.btnPark,
            binding.btnOthers,
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                toggleSelection(button)
            }
        }

    }

    override fun addEvent() {
        binding.tvNext.setOnClickListener {
            saveSelectedInterests()
        }
    }

    private fun toggleSelection(button: Button) {
        val isSelected = selectedInterests.contains(button.text.toString())
        if (isSelected) {
            selectedInterests.remove(button.text.toString())
            button.setBackgroundResource(R.drawable.button_unselected)
            button.setTextColor(Color.BLACK)
        } else {
            selectedInterests.add(button.text.toString())
            button.setBackgroundResource(R.drawable.button_selected)
            button.setTextColor(Color.WHITE)
        }
    }

    private fun saveSelectedInterests() {
//        val intent = Intent(this, OnBoardActivity::class.java)
//        intent.putStringArrayListExtra("selectedInterests", ArrayList(selectedInterests))
//        startActivity(intent)
        OnBoardActivity.start(this)
        finish()
    }
}