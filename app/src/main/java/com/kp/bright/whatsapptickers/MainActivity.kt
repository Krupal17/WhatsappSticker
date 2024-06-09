package com.kp.bright.whatsapptickers

import android.Manifest.*
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kp.bright.whatsapptickers.whatsappsticker.ADD_PACK
import com.kp.bright.whatsapptickers.whatsappsticker.addStickerPackToWhatsApp
import com.kp.bright.whatsapptickers.whatsappsticker.copyAllAssetsToExternalStorage
import com.kp.bright.whatsapptickers.whatsappsticker.copyAssetsToExternalStorage
import com.kp.bright.whatsapptickers.whatsappsticker.createStickerPack
import com.kp.bright.whatsapptickers.whatsappsticker.initPacks
import com.kp.bright.whatsapptickers.whatsappsticker.loadStickerPack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {
    var REQUEST_CODE = 600
    var identifier = "States"
//    var identifier = "Animations"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initview()
    }

    private fun initview() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission.READ_MEDIA_IMAGES),
                    REQUEST_CODE
                )
            } else {
                val pack = File(
                    getExternalFilesDir(null),
                    "$identifier"
                )
                initPacks(this)
                if (!pack.exists()) {
//                    copyAssetsToExternalStorage(this, identifier)
                    copyAllAssetsToExternalStorage(this)
                } else {
                    pack.mkdirs()
                }
            }
        } else {

            if (ContextCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission.READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
            } else {
                val pack = File(
                    getExternalFilesDir(null),
                    "$identifier"
                )
                initPacks(this)
                if (!pack.exists()) {
//                    copyAssetsToExternalStorage(this, identifier)
                    copyAllAssetsToExternalStorage(this)
                }
            }
        }

        findViewById<Button>(R.id.btnWhatsappSticker).setOnClickListener {
            val pack = File(
                getExternalFilesDir(null),
                identifier
            )
            if (pack.exists()) {
                var stickerPaths = ArrayList<String>()
                var iconPath = ""
                lifecycleScope.launch {
//                    pack.listFiles().forEach {
//                        if (it.name.contains("trayicon")) {
//                            iconPath = it.name
//                        } else {
//                            stickerPaths.add(it.name)
//                        }
//                    }
                    var metadata = loadStickerPack(this@MainActivity, identifier)
//                    createStickerPack(
//                        identifier,
//                        "${identifier}",
//                        "kpStickers",
//                        iconPath,
//                        stickerPaths,
//                        this@MainActivity
//                    )
                    withContext(Dispatchers.Main) {
//                        addStickerPackToWhatsApp(this@MainActivity, identifier,"KrupalDemo")
                        addStickerPackToWhatsApp(metadata?.identifier, metadata?.name);
                    }
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_PACK) {
            if (resultCode == Activity.RESULT_OK) {

            } else {
                if (data != null) {
                    val validationError = data.getStringExtra("validation_error")
                    Log.e("TAG", "validationError: $validationError")
                    AlertDialog.Builder(this).setTitle("WA Alert").setMessage(validationError)
                        .show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, proceed with the operation
                val pack = File(
                    getExternalFilesDir(null),
                    "$identifier"
                )
                initPacks(this)
                if (!pack.exists()) {
//                    copyAssetsToExternalStorage(this, identifier)
                    copyAllAssetsToExternalStorage(this)
                }
            } else {
                // Permission denied, handle accordingly
            }
        }
    }

}