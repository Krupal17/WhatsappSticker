package com.kp.bright.whatsapptickers.toolkit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kp.bright.whatsapptickers.databinding.ActivityToolBinding
import com.kp.bright.whatsapptickers.tabulas.PdfScannActivity
import com.kp.bright.whatsapptickers.wasticker.MainActivity
import com.kp.bright.whatsapptickers.wifisettings.WifiActivity

class ToolActivity : AppCompatActivity() {
    val binding: ActivityToolBinding by lazy {
        ActivityToolBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContentView(binding.root)

        initClick()

    }

    private fun initClick() {
        binding.apply {
            btnInAppHotspotDemo.setOnClickListener {
                startActivity(Intent(this@ToolActivity, WifiActivity::class.java))
            }

            btnWhatsappStickerDemo.setOnClickListener {
                startActivity(Intent(this@ToolActivity, MainActivity::class.java))
            }
            btnStatementScanDemo.setOnClickListener {
                startActivity(Intent(this@ToolActivity, PdfScannActivity::class.java))
            }

        }
    }
}