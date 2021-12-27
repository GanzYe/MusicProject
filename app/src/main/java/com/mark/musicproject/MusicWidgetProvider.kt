package com.mark.musicproject

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.mark.musicproject.services.PlayerService

class MusicWidgetProvider: AppWidgetProvider() {

    companion object{
        const val ACTION_PLAY = "${BuildConfig.APPLICATION_ID}.ACTION_WIDGET_PLAY"
        const val ACTION_PAUSE = "${BuildConfig.APPLICATION_ID}.ACTION_WIDGET_PAUSE"
        const val ACTION_STOP = "${BuildConfig.APPLICATION_ID}.ACTION_WIDGET_STOP"

        const val EXTRA_SONG_NAME = "song_name"
        const val SONG_NAME_DEFAULT = "NO song playing"
    }

    private var songName: String = SONG_NAME_DEFAULT
    private var isPlaying: Boolean = false

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {

        appWidgetIds?.forEach {
            appWidgetManager?.updateAppWidget(it, getRemoteViews(context))
        }

    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        var remoteViews: RemoteViews? =null
        when(intent?.action){
            ACTION_PLAY->{
                isPlaying=true
                songName = intent.getStringExtra(EXTRA_SONG_NAME)?:songName
                remoteViews = getRemoteViews(context)
            }
            ACTION_PLAY->{
                isPlaying=false
                remoteViews = getRemoteViews(context)
            }
            ACTION_PLAY->{
                isPlaying=true
                songName = SONG_NAME_DEFAULT
                remoteViews = getRemoteViews(context)
            }
        }
        remoteViews?.also {
            AppWidgetManager.getInstance(context).updateAppWidget(
                ComponentName(context?:return@also, MusicWidgetProvider::class.java),it
            )
        }
    }

    private fun getRemoteViews(context: Context?):RemoteViews{
        val playPauseIntent: Intent = if (isPlaying){
            Intent(PlayerService.ACTION_PAUSE)
        }else{
            Intent(PlayerService.ACTION_PLAY)
        }

        val playPausePendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            playPauseIntent,
            0
        )

        val views = RemoteViews(context?.packageName, R.layout.widget_music).apply{
            setOnClickPendingIntent(R.id.button_play_pause, playPausePendingIntent)
            setImageViewResource(
                R.id.button_play_pause,
                if(isPlaying)
                    R.drawable.ic_pause_white
                else
                    R.drawable.ic_play_white
            )
            setTextViewText(R.id.texture_view,songName)
        }
        return views
    }

}