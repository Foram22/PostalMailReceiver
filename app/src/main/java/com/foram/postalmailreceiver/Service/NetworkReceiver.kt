package com.foram.postalmailreceiver.Service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.foram.postalmailreceiver.MyApplication

class NetworkReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        if (!isNetworkAvailable(context)) {
            // Show alert dialog or start a service to handle this
            context.startService(Intent(context, AlertService::class.java))
        } else {
            MyApplication.shouldPauseSplashNavigation = false
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}