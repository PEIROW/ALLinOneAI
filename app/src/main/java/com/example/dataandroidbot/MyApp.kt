package com.example.dataandroidbot

import android.app.Application
import com.example.dataandroidbot.util.NotificationHelper

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
    }
}
