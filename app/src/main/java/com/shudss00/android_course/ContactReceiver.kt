package com.shudss00.android_course

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class ContactReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Toast.makeText(context, "Здарова работяга!!!", Toast.LENGTH_SHORT).show()
        }
        if (intent.action == "birthdayNotifyAction") {
            val contactId = intent.extras!!.getInt("CONTACT_ID")
            val message = intent.getStringExtra("MESSAGE")
            createNotificationChannel(context)
            val intentToActivity = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("CONTACT_ID", contactId)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                contactId,
                intentToActivity,
                PendingIntent.FLAG_IMMUTABLE
            )
            sendNotification(context, contactId, message, pendingIntent)
        }

    }

    private fun sendNotification(
        context: Context,
        notificationId: Int,
        message : String?,
        pendingIntent: PendingIntent
    ) = with(context) {
            val builder = NotificationCompat.Builder(
                this,
                getString(R.string.notification_channel_id)
            )
                .setSmallIcon(R.drawable.ic_baseline_cake_24)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            NotificationManagerCompat.from(this)
                .notify(notificationId, builder.build())
        }

    private fun createNotificationChannel(context: Context) {
        with(context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = getString(R.string.notification_channel_id)
                val name = getString(R.string.notification_channel_name)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel  = NotificationChannel(channelId, name, importance)
                val notificationManager: NotificationManager =
                    getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
