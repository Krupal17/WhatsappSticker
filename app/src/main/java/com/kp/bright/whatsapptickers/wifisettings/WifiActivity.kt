package com.kp.bright.whatsapptickers.wifisettings

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.kp.bright.whatsapptickers.databinding.ActivityWifiBinding
import com.kp.bright.whatsapptickers.wifisettings.HotspotUtils.checkAndRequestPermissions
import com.kp.bright.whatsapptickers.wifisettings.HotspotUtils.isHotspotEnabled
import com.kp.bright.whatsapptickers.wifisettings.HotspotUtils.toggleHotspot

class WifiActivity : AppCompatActivity() {
    val binding: ActivityWifiBinding by lazy {
        ActivityWifiBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(binding.root)

        initView()

    }

    private fun initView() {

        if (checkAndRequestPermissions(this)) {
            if (isHotspotEnabled(this)) {
                binding.btnHotpost.text = "Off Hotspot"
            } else {
                binding.btnHotpost.text = "On Hotspot"
            }
        }

        binding.btnHotpost.setOnClickListener {
            if (checkAndRequestPermissions(this)) {
                toggleHotspot(this, { b: Boolean ->
                    Log.e("TAG", "initView: $b")

                },{
                    Log.e("TAG", "initView: -->$it", )
                    if (it) {
                        binding.btnHotpost.text = "Off Hotspot"
                    } else {
                        binding.btnHotpost.text = "On Hotspot"
                    }
                })
            }
        }
    }


}