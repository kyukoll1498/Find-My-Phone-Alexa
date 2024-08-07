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
        binding.tvNext.setSize(18)
        binding.tvNext.paintFlags = Paint.UNDERLINE_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG

        selectedInterests = mutableListOf()

        // Initialize buttons
        val buttons = listOf(
            binding.btnAnimal,
            binding.btnMusic,
            binding.btnParty,
            binding.btnCar,
            binding.btnTravel,
            binding.btnDrawing,
            binding.btnTechnology,
            binding.btnGame,
            binding.btnArt,
            binding.btnHomeDecor,
            binding.btnFood,
            binding.btnFunny,
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