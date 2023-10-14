package com.foram.postalmailreceiver

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult
import com.foram.postalmailreceiver.Service.NetworkReceiver
import com.google.firebase.FirebaseApp

class MyApplication : Application() {

    lateinit var intentFilter: IntentFilter
    lateinit var networkReceiver: NetworkReceiver

    companion object {
        var shouldPauseSplashNavigation: Boolean = true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
            Log.d("TAG", "Firebase initialized")
        }


        networkReceiver = NetworkReceiver()
        intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)

        registerReceiver(networkReceiver, intentFilter)
    }


    override fun onTerminate() {
        super.onTerminate()

        unregisterReceiver(networkReceiver)
    }
}