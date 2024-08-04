package com.kp.bright.whatsapptickers.wifisettings

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.util.Log
import com.kp.bright.whatsapptickers.wifisettings.bal.stock.ProxyBuilder

object HotspotUtils {


    fun checkAndRequestPermissions(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:${context.packageName}")
                context.startActivity(intent)
                return false
            }
        }
        return true
    }

    fun enableWifiTethering(context: Context, onDone: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val method = connectivityManager.javaClass.getDeclaredMethod(
                    "startTethering",
                    Int::class.javaPrimitiveType,
                    Boolean::class.javaPrimitiveType,
                    onStartTetheringCallbackClass(),
                    Handler::class.java
                )
                method.isAccessible = true

                // Create a proxy instance of OnStartTetheringCallback
                val callbackClass = onStartTetheringCallbackClass()
                if (callbackClass != null) {
                    val callbackInstance = ProxyBuilder.forClass(callbackClass)
                        .dexCache(context.codeCacheDir)
                        .handler { proxy, method, args ->
                            when (method.name) {
                                "onTetheringStarted" -> onDone(true)
                                "onTetheringFailed" -> onDone(false)
                            }
                            null
                        }
                        .build()

                    method.invoke(connectivityManager, 0, false, callbackInstance, null)
                } else {
                    throw IllegalStateException("Callback class not found")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("TAG", "enableWifiTethering: $e")
                onDone(false)
            }
        } else {
            // Use reflection for older Android versions
            try {
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiConfig = WifiConfiguration().apply {
                    SSID = "\"MyHotspot\""
                    preSharedKey = "\"password\""
                    allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                }
                val method = wifiManager.javaClass.getMethod(
                    "setWifiApEnabled",
                    WifiConfiguration::class.java,
                    Boolean::class.javaPrimitiveType
                )
                method.isAccessible = true
                val result = method.invoke(wifiManager, wifiConfig, true) as Boolean
                onDone(result)
            } catch (e: Exception) {
                e.printStackTrace()
                onDone(false)
            }
        }
    }

    fun disableWifiTethering(context: Context, onDone: (Boolean) -> Unit) {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val method = connectivityManager.javaClass.getDeclaredMethod("stopTethering", Int::class.javaPrimitiveType)
            method.isAccessible = true
            method.invoke(connectivityManager, 0)
            onDone(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "disableWifiTethering: $e")
        }
    }

    fun isHotspotEnabled(context: Context): Boolean {
        return try {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val method = wifiManager.javaClass.getDeclaredMethod("isWifiApEnabled")
            method.isAccessible = true
            val result = method.invoke(wifiManager)
            result as Boolean
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "isHotspotEnabled: $e")
            false
        }

    }

    fun toggleHotspot(context: Context, onDone: (Boolean) -> Unit,onToggle:(Boolean)->Unit) {
        if (isHotspotEnabled(context)) {
            disableWifiTethering(context) { success ->
                onDone(success)
                onToggle(false)
            }
        } else {
            enableWifiTethering(context) { success ->
                onDone(success)
                onToggle(true)

            }
        }
    }


    private fun onStartTetheringCallbackClass(): Class<*>? {
        return try {
            Class.forName("android.net.ConnectivityManager\$OnStartTetheringCallback")
        } catch (unused: ClassNotFoundException) {
            null
        }
    }

}
