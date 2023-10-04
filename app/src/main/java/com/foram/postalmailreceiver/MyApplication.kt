package com.foram.postalmailreceiver

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (FirebaseApp.getApps(this).isEmpty()){
            FirebaseApp.initializeApp(this)
            Log.d("TAG", "Firebase initialized")
        }
    }
}