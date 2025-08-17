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
import com.common.control.utils.BroadcastUtils
import com.mtg.tool.findmyphone.ACTION_UPDATE_AUDIO_IMPORT
import com.mtg.tool.findmyphone.CREATE_SOUND_TYPE
import com.mtg.tool.findmyphone.KEY_SOUND
import com.mtg.tool.findmyphone.KEY_SOUND_ITEM_DATA
import com.mtg.tool.findmyphone.R
import com.mtg.tool.findmyphone.base.BaseActivity
import com.mtg.tool.findmyphone.data.model.SoundItem
import com.mtg.tool.findmyphone.data.repo.AppRepository
import com.mtg.tool.findmyphone.databinding.FragmentAddBinding
import com.mtg.tool.findmyphone.main.activity.CreateSoundActivity
import com.mtg.tool.findmyphone.main.activity.PlaySoundActivity
import com.mtg.tool.findmyphone.main.adapter.SoundAdapter

class AddFragment : BaseActivity<FragmentAddBinding>(FragmentAddBinding::inflate) {
    private var soundList = mutableListOf<SoundItem>()
    private var soundAdapter: SoundAdapter? = null

    companion object {
        private var createSoundItem = SoundItem(
            CREATE_SOUND_TYPE, "", 0, R.drawable.image_sound_create, 0, ""
        )

        @JvmStatic
        fun start(context: Context) {
            val starter = Intent(context, AddFragment::class.java)
            context.startActivity(starter)
        }
    }

    private val soundReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("NotifyDataSetChanged")
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.getSerializableExtra(KEY_SOUND)
            if (data == null) {
                if (soundList.size == 1) {
                    soundList.removeAt(0)
                    binding?.ctEmpty?.visibility = View.VISIBLE
                    binding?.rcvSoundImport?.visibility = View.GONE
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

//    override fun loadAds() {
//        super.loadAds()
//        AdmobManager.getInstance().loadNative(
//            context,
//            BuildConfig.native_add,
//            binding.frAd,
//            R.layout.custom_native_ads_30
//        )
//        AppOpenManager.getInstance().hideNativeOrBannerWhenShowOpenApp(requireActivity(), binding.frAd)
//    }

    override fun initView() {
        createSoundItem.name = getString(R.string.create_new)
        loadSoundList()
        registerBroadcast()
    }

    private fun registerBroadcast() {
        BroadcastUtils.registerReceiver(
            this, soundReceiver, IntentFilter(ACTION_UPDATE_AUDIO_IMPORT)
        )
    }

    private fun loadSoundList() {
        Thread {
            soundList = AppRepository.getAllSoundImport()
            runOnUiThread {
                initContent()
            }
        }.start()
    }

    private fun initContent() {
        if (soundList.isEmpty()) {
            binding?.ctEmpty?.visibility = View.VISIBLE
            binding?.rcvSoundImport?.visibility = View.GONE
        } else {
            binding?.ctEmpty?.visibility = View.GONE
            binding?.rcvSoundImport?.visibility = View.VISIBLE
            if (!soundList.contains(createSoundItem)) {
                soundList.add(
                    0, createSoundItem
                )
            }
            soundAdapter = SoundAdapter(soundList, this)
            soundAdapter?.mCallback = OnActionCallback { key, data ->
                if (key.equals(KEY_SOUND)) {
                    logEvent("add_sound_click")
                    var soundItem = data[0] as SoundItem
                    if (soundItem.type == CREATE_SOUND_TYPE) {
                        logEvent("click_add_create_new")
                        startActivity(Intent(this, CreateSoundActivity::class.java))
                    } else {
                        logEvent("click_add_open_audio")
                        var intent = Intent(this, PlaySoundActivity::class.java)
                        intent.putExtra(KEY_SOUND_ITEM_DATA, soundItem)
                        startActivity(intent)
                    }

                }
            }
            binding?.rcvSoundImport?.layoutManager = GridLayoutManager(this, 3)
            binding?.rcvSoundImport?.adapter = soundAdapter
        }
    }

    private fun setUpResponsive() {
        binding?.let { binding ->
            if (binding.tvEmpty.y + binding.tvEmpty.height > binding.ivGuideCreate.y) {
                binding.ivGuideCreate.visibility = View.GONE
            }
        }
    }

    override fun addEvent() {
        binding?.llCreateSound?.setOnClickListener {
            logEvent("add_create_click")
            startActivity(Intent(this, CreateSoundActivity::class.java))
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
        unregisterReceiver(soundReceiver)
    }

}