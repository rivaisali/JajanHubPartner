package com.arajangstudio.jajanhub_partner.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.arajangstudio.jajanhub_partner.R
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.concurrent.atomic.AtomicInteger

class FirebaseMessageReceiver : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            sendNotification(remoteMessage)
        }

        // Check if message contains a notification payload.
        remoteMessage.data.let {
            Log.d(TAG, "Message Notification Body: ${it["message"]}")
        }

    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    override fun handleIntent(intent: Intent) {
        try {
            if (intent.extras != null) {
                val builder = RemoteMessage.Builder("FirebaseMessageReceiver")
                for (key in intent.extras!!.keySet()) {
                    builder.addData(key!!, intent.extras!![key].toString())
                }
                onMessageReceived(builder.build())
            } else {
                super.handleIntent(intent)
            }
        } catch (e: Exception) {
            super.handleIntent(intent)
        }

    }

    private fun scheduleJob() {
        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
    }

    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    private fun sendRegistrationToServer(token: String?) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun sendNotification(remoteMessage: RemoteMessage) {

        val actionClick = remoteMessage.notification?.clickAction
        val intent = Intent(actionClick)
        intent.putExtra("extra_id", remoteMessage.data.get("id"))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "godigiChannel"
        val largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle(remoteMessage.notification?.title)
            .setContentText(remoteMessage.notification?.body)
            .setAutoCancel(true)
            .setShowWhen(true)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Miscellaneous",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        if (remoteMessage.notification?.imageUrl != null) {
            Glide.with(applicationContext)
                .asBitmap()
                .load(remoteMessage.notification?.imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        //largeIcon
                        notificationBuilder.setLargeIcon(resource)
                        notificationBuilder.setSmallIcon(R.drawable.icon_notification)
                        //Big Picture
                        notificationBuilder.setStyle(
                            NotificationCompat.BigPictureStyle().bigPicture(resource)
                        )
                        notificationManager.notify(NotificationID.iD, notificationBuilder.build())
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        } else {
            notificationBuilder.setSmallIcon(R.drawable.icon_notification)
            notificationBuilder.setLargeIcon(largeIcon)
            notificationManager.notify(NotificationID.iD, notificationBuilder.build())
        }

    }

    companion object {

        private const val TAG = "MyFirebaseMsgService"
    }

    internal object NotificationID {
        private val c = AtomicInteger(100)
        val iD: Int
            get() = c.incrementAndGet()
    }
}