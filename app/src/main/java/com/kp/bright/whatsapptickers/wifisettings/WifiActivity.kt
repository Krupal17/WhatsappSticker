package com.kp.bright.whatsapptickers.wifisettings

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.kp.bright.whatsapptickers.R
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

        binding.btnOpenDialog.setOnClickListener {
            openCustomDialog()
        }
        binding.btnHotpost.setOnClickListener {
            if (checkAndRequestPermissions(this)) {
                toggleHotspot(this, { b: Boolean ->
                    Log.e("TAG", "initView: $b")

                }, {
                    Log.e("TAG", "initView: -->$it")
                    if (it) {
                        binding.btnHotpost.text = "Off Hotspot"
                    } else {
                        binding.btnHotpost.text = "On Hotspot"
                    }
                })
            }
        }
    }

    private fun openCustomDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_dialog)

        val editText = dialog.findViewById<EditText>(R.id.editText)
        val btnOk = dialog.findViewById<Button>(R.id.btnOk)

        // Make dialog cancelable
        dialog.setCancelable(true)

        // Show keyboard when the dialog opens
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        // Show the keyboard immediately
        editText.requestFocus()

        btnOk.setOnClickListener {
            val word = editText.text.trim()
            if (word.isNotBlank()) {
                Toast.makeText(this, word.toString(), Toast.LENGTH_SHORT).show()

                dialog.dismiss()
            }
        }
        // Show the dialog
        dialog.show()

        // Hide keyboard and dismiss dialog on touch outside
        dialog.setOnCancelListener {
            hideKeyboardAndDismiss(dialog, editText)
        }

        dialog.window?.decorView?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (event.rawX < editText.left || event.rawX > editText.right || event.rawY < editText.top || event.rawY > editText.bottom) {
                    hideKeyboardAndDismiss(dialog, editText)
                    true
                }
            }
            false
        }
    }

    private fun hideKeyboardAndDismiss(dialog: Dialog, editText: EditText) {
        // Hide the keyboard
        val imm = getSystemService<InputMethodManager>()
        imm?.hideSoftInputFromWindow(editText.windowToken, 0)

        // Delay dismiss to allow the keyboard to fully hide
        editText.postDelayed({
            dialog.dismiss()
        }, 200)  // Adjust delay as needed
    }

}