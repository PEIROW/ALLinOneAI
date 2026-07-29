package com.example.dataandroidbot.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.dataandroidbot.MainActivity
import com.example.dataandroidbot.R

object NotificationHelper {

    const val CHANNEL_AI_RESPONSES = "ai_responses"
    const val CHANNEL_ERRORS = "ai_errors"
    const val CHANNEL_GENERAL = "general"

    private const val NOTIFICATION_ID_AI_RESPONSE = 1001
    private const val NOTIFICATION_ID_ERROR = 1002

    const val TYPE_AI_RESPONSE = "ai_response"
    const val TYPE_ERROR = "error"
    const val TYPE_MISSING_KEY = "missing_key"
    const val TYPE_GENERAL = "general"

    const val EXTRA_FROM_NOTIFICATION = "from_notification"
    const val EXTRA_NOTIFICATION_TYPE = "notification_type"
    const val EXTRA_ERROR_MESSAGE = "error_message"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val responsesChannel = NotificationChannel(
            CHANNEL_AI_RESPONSES,
            "AI Responses",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications when an AI model finishes generating a reply"
            enableVibration(true)
        }

        val errorsChannel = NotificationChannel(
            CHANNEL_ERRORS,
            "Errors & Warnings",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Important errors such as API failures or missing keys"
        }

        val generalChannel = NotificationChannel(
            CHANNEL_GENERAL,
            "General",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "General app notifications"
            setShowBadge(false)
        }

        notificationManager.createNotificationChannels(
            listOf(responsesChannel, errorsChannel, generalChannel)
        )
    }

    fun canShowNotifications(context: Context): Boolean {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return false
        }

        return true
    }

    private fun createContentIntent(
        context: Context,
        notificationType: String,
        errorMessage: String? = null
    ): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_FROM_NOTIFICATION, true)
            putExtra(EXTRA_NOTIFICATION_TYPE, notificationType)
            if (errorMessage != null) {
                putExtra(EXTRA_ERROR_MESSAGE, errorMessage)
            }
        }

        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        return PendingIntent.getActivity(
            context,
            notificationType.hashCode(),
            intent,
            flags
        )
    }

    fun showAiResponseNotification(
        context: Context,
        title: String = "AI Reply Ready",
        message: String,
        modelName: String? = null
    ) {
        if (!canShowNotifications(context)) return

        val fullMessage = if (modelName != null) {
            "$message\n\nModel: $modelName"
        } else message

        val contentIntent = createContentIntent(context, TYPE_AI_RESPONSE)

        val notification = NotificationCompat.Builder(context, CHANNEL_AI_RESPONSES)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(fullMessage)
            .setStyle(NotificationCompat.BigTextStyle().bigText(fullMessage))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID_AI_RESPONSE, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun showErrorNotification(
        context: Context,
        title: String = "AI Bot Error",
        message: String,
        isMissingKey: Boolean = false
    ) {
        if (!canShowNotifications(context)) return

        val type = if (isMissingKey) TYPE_MISSING_KEY else TYPE_ERROR
        val contentIntent = createContentIntent(context, type, message)

        val notification = NotificationCompat.Builder(context, CHANNEL_ERRORS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID_ERROR, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
