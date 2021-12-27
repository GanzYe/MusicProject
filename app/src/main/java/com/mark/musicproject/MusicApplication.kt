package com.mark.musicproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.room.Room
import com.mark.musicproject.db.AppDatabase

class MusicApplication : Application() {

    companion object{
        const val CHANNEL_ID = "music_channel"

        lateinit var instance: MusicApplication
    }

    lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()

        instance = this

        db = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "music_database"
        ).fallbackToDestructiveMigration().build()

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = getString(R.string.music_notif_channel_name)
            val descriptionText = getString(R.string.music_notif_channel_desc)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

}