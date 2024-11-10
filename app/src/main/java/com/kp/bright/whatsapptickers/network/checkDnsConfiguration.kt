package com.kp.bright.whatsapptickers.network

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log

fun isPrivateDns(context: Context): Boolean {
    return if (checkDnsMode(context) == "DNS Mode: Auto/Off") {
        false
    } else {
        true
    }
}

fun checkDnsMode(context: Context): String {
    var dns = getPrivateDnsStatus(context)
    android.util.Log.e("DNS-->", "checkDnsMode: $dns")
    return dns
}

private fun getPrivateDnsStatus(context: Context): String {
    return try {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val linkProperties =
            connectivityManager.getLinkProperties(connectivityManager.activeNetwork)

        // Check Private DNS hostname
        val privateDnsServerName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            linkProperties?.privateDnsServerName
        } else {
            null
        }

        if (privateDnsServerName.isNullOrBlank()) {
            "DNS Mode: Auto/Off"
        } else {
            "DNS Mode: Private DNS (Custom Host: $privateDnsServerName)"
        }
    } catch (e: Exception) {
        Log.e("DNSCheck", "Error checking DNS mode", e)
        "Error: Unable to determine DNS mode"
    }
}