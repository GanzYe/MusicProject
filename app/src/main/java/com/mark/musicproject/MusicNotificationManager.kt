package com.mark.musicproject

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mark.musicproject.db.models.TrackItem
import com.mark.musicproject.services.PlayerService

class MusicNotificationManager(private val context: Context) {

    companion object{

        const val REQUEST_CODE = 1337
        const val NOTIFICATION_ID = 123321

    }

    private val notificationBuilder: NotificationCompat.Builder

    init {
        val intent = Intent(context, ServiceActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, intent, 0)

        notificationBuilder = NotificationCompat.Builder(context, MusicApplication.CHANNEL_ID)
            .setContentTitle("Music service")
            .setContentText("Service running")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }

    fun showNotification(track: TrackItem, isPlaying: Boolean){
        notificationBuilder.setContentText(track.name)
        notificationBuilder.setContentTitle(track.author)
        notificationBuilder.setSilent(true)

        val playPauseIcon: Int
        val playPauseName: String
        val playPauseIntent: Intent
        if(isPlaying){
            playPauseIcon = R.drawable.ic_pause
            playPauseName = context.getString(R.string.pause)
            playPauseIntent = Intent(PlayerService.ACTION_PAUSE)
        } else {
            playPauseIcon = R.drawable.ic_play
            playPauseName = context.getString(R.string.play)
            playPauseIntent = Intent(PlayerService.ACTION_PLAY)
        }
        val playPausePendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, playPauseIntent, 0)

        val stopIntent = Intent(PlayerService.ACTION_STOP)
        val stopPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, stopIntent, 0)

        notificationBuilder.clearActions()
        notificationBuilder.addAction(playPauseIcon, playPauseName, playPausePendingIntent)
        notificationBuilder.addAction(R.drawable.ic_stop, context.getString(R.string.stop), stopPendingIntent)

        notificationBuilder.setStyle(androidx.media.app.NotificationCompat.MediaStyle()
            .setCancelButtonIntent(stopPendingIntent)
            .setShowCancelButton(true))

        with(NotificationManagerCompat.from(context)){
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    fun getNotification() : Notification{
        return notificationBuilder.build()
    }

}