package com.mahilashakti.unnati.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "SHG Alert"
        val message = intent.getStringExtra("message") ?: "You have an upcoming task."
        
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(title, message)
    }
}
