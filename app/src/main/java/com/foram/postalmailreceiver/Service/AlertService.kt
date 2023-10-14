package com.foram.postalmailreceiver.Service

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import com.foram.postalmailreceiver.R

class AlertService : android.app.Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Ensure you're running on the Main thread to interact with UI
        Handler(Looper.getMainLooper()).post {
            val view: View = LayoutInflater.from(applicationContext)
                .inflate(R.layout.no_internet_connection_alert_dialog, null, false)
            val dialog = Dialog(
                applicationContext
            ).apply {
                window?.setType(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            }
            dialog.setCancelable(false)
            dialog.setContentView(view)

            val btnRetry: Button = view.findViewById(R.id.btnRetry)
            val btnCancel: Button = view.findViewById(R.id.btnCancel)

            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            btnRetry.setOnClickListener {
                openNetworkSetting()
                dialog.dismiss()
            }

            dialog.show()
        }
        return START_NOT_STICKY
    }

    private fun openNetworkSetting() {
        val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}