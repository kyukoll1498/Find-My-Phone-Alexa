package com.mtg.tool.findmyphone.main.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.common.control.base.OnActionCallback
import com.common.control.manager.AdmobManager
import com.common.control.manager.AppOpenManager
import com.mtg.tool.findmyphone.ACTION_UPDATE_AUDIO_IMPORT
import com.mtg.tool.findmyphone.BuildConfig
import com.mtg.tool.findmyphone.CREATE_SOUND_TYPE
import com.mtg.tool.findmyphone.KEY_SOUND
import com.mtg.tool.findmyphone.KEY_SOUND_ITEM_DATA
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseFragment
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.data.repo.AppRepository
import com.mtg.tool.findmyphone.databinding.FragmentAddBinding
import com.mtg.tool.findmyphone.main.activity.CreateSoundActivity
import com.mtg.tool.findmyphone.main.activity.PlaySoundActivity
import com.mtg.tool.findmyphone.main.adapter.SoundAdapter

class AddFragment : BaseFragment<FragmentAddBinding>(FragmentAddBinding::inflate) {
    private var soundList = mutableListOf<SoundItem>()
    private var soundAdapter: SoundAdapter? = null

    private val soundReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.getSerializableExtra(KEY_SOUND)
            if (data == null) {
                if (soundList.size == 1) {
                    soundList.removeAt(0)
                    binding.ctEmpty.visibility = View.VISIBLE
                    binding.rcvSoundImport.visibility = View.GONE
                }
                soundAdapter?.notifyDataSetChanged()
            } else {
                if (soundList.size == 1) {
                    initContent()
                } else {
                    soundAdapter?.notifyDataSetChanged()
                }
            }

        }
    }

    override fun loadAds() {
        super.loadAds()
        AdmobManager.getInstance().loadNative(
            context,
            BuildConfig.native_add,
            binding.frAd,
            R.layout.custom_native_ads_30
        )
        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(requireActivity(), binding.frAd)
    }

    override fun initView() {
        loadSoundList()
        registerBroadcast()
    }

    private fun registerBroadcast() {
        activity?.registerReceiver(soundReceiver, IntentFilter(ACTION_UPDATE_AUDIO_IMPORT))
    }

    private fun loadSoundList() {
        Thread {
            soundList = AppRepository.getAllSoundImport()
            activity?.runOnUiThread {
                initContent()
            }
        }.start()
    }

    private fun initContent() {
        if (soundList.isEmpty()) {
            binding.ctEmpty.visibility = View.VISIBLE
            binding.rcvSoundImport.visibility = View.GONE
        } else {
            binding.ctEmpty.visibility = View.GONE
            binding.rcvSoundImport.visibility = View.VISIBLE
            soundList.add(
                0, SoundItem(
                    CREATE_SOUND_TYPE,
                    requireActivity().getString(R.string.create_new),
                    0,
                    R.drawable.image_sound_create,
                    0,
                    ""
                )
            )
            soundAdapter = SoundAdapter(soundList, requireActivity())
            soundAdapter?.mCallback = OnActionCallback { key, data ->
                if (key.equals(KEY_SOUND)) {
                    var soundItem = data[0] as SoundItem
                    if (soundItem.type == CREATE_SOUND_TYPE) {
                        logEvent("click_add_create_new")
                        startActivity(Intent(activity, CreateSoundActivity::class.java))
                    } else {
                        logEvent("click_add_open_audio")
                        var intent = Intent(activity, PlaySoundActivity::class.java)
                        intent.putExtra(KEY_SOUND_ITEM_DATA, soundItem)
                        startActivity(intent)
                    }

                }
            }
            binding.rcvSoundImport.layoutManager = GridLayoutManager(context, 3)
            binding.rcvSoundImport.adapter = soundAdapter
        }
    }

    private fun setUpResponsive() {
        if (binding.tvEmpty.y + binding.tvEmpty.height > binding.ivGuideCreate.y) {
            binding.ivGuideCreate.visibility = View.GONE
        }
    }

    override fun addEvent() {
        binding.llCreateSound.setOnClickListener {
            logEvent("click_add_create_sound")
            startActivity(Intent(activity, CreateSoundActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        setUpResponsive()
        Handler().postDelayed(Runnable {
            try {
                setUpResponsive()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 500)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unregisterReceiver(soundReceiver)
    }

}