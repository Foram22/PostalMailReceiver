@file:Suppress("DEPRECATION")

package com.foram.postalmailreceiver.Service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.foram.postalmailreceiver.Activity.MainActivity
import com.foram.postalmailreceiver.R
import com.google.firebase.messaging.RemoteMessage

@Suppress("DEPRECATION")
class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    lateinit var notifManager: NotificationManager
    lateinit var notifChannel: NotificationChannel
    lateinit var notifBuilder: Notification.Builder

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        showNotification(message)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun showNotification(message: RemoteMessage) {

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        notifManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val imageUrl = message.notification?.imageUrl

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifChannel =
                NotificationChannel("123", "Postal Mail", NotificationManager.IMPORTANCE_HIGH)
            notifChannel.enableLights(true)
            notifChannel.enableVibration(true)
            notifChannel.lightColor = Color.BLUE

            notifManager.createNotificationChannel(notifChannel)

            Glide.with(this).asBitmap().load(imageUrl).into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    notifBuilder = Notification.Builder(this@FirebaseMessagingService, "123")
                        .setContentTitle(message.notification?.title)
                        .setContentText(message.notification?.body).setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_email)
                        .setLargeIcon(resource)
                        .setStyle(Notification.BigPictureStyle().bigPicture(resource))
                        .setContentIntent(pendingIntent)
                }
            })

        } else {
            notifBuilder = Notification.Builder(this)
                .setContentTitle(message.notification?.title)
                .setContentText(message.notification?.body).setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_email)
                .setContentIntent(pendingIntent)
        }

        notifManager.notify(1234, notifBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}