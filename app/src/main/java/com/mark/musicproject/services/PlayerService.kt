package com.mark.musicproject.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.mark.musicproject.BuildConfig
import com.mark.musicproject.MusicNotificationManager
import com.mark.musicproject.MusicWidgetProvider
import com.mark.musicproject.db.models.TrackItem

class PlayerService : Service(), Player.Listener {

    companion object{
        const val ACTION_PLAY = "${BuildConfig.APPLICATION_ID}.ACTION_PLAY"
        const val ACTION_PAUSE = "${BuildConfig.APPLICATION_ID}.ACTION_PAUSE"
        const val ACTION_STOP = "${BuildConfig.APPLICATION_ID}.ACTION_STOP"
    }

    private lateinit var notificationManager: MusicNotificationManager

    private val binder = ServiceBinder()

    private lateinit var player: SimpleExoPlayer

    private var currentTrack: TrackItem? = null

    override fun onCreate() {
        super.onCreate()

        notificationManager = MusicNotificationManager(this)
        startForeground(MusicNotificationManager.NOTIFICATION_ID, notificationManager.getNotification())

        player = SimpleExoPlayer.Builder(this).build()
        player.addListener(this)
    }

    fun play(track: TrackItem){
        currentTrack = track
        player.addMediaItem(MediaItem.fromUri(track.uri))
        player.prepare()
        player.play()

        notificationManager.showNotification(currentTrack ?: return, player.isPlaying)
    }

    override fun onPlayerError(error: PlaybackException) {
        Log.e("PlaybackError", error.message ?: "")
    }

    fun pause(){
        player.pause()

        notificationManager.showNotification(currentTrack ?: return, player.isPlaying)

    }

    override fun onDestroy() {
        super.onDestroy()

        stop()
        player.release()
    }
    fun resume(){
        player.play()

        notificationManager.showNotification(currentTrack ?: return, player.isPlaying)
    }

    fun stop(){
        player.stop()
        player.clearMediaItems()
        //player.release()
        currentTrack = null
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        sendBroadcast()
    }

    private fun sendBroadcast(){
        sendBroadcast(Intent(this, MusicWidgetProvider::class.java).apply {
            action = if(currentTrack!= null){
                if(player.isPlaying){
                    MusicWidgetProvider.ACTION_PLAY
                }else{
                    MusicWidgetProvider.ACTION_PAUSE
                }
            }else{
                MusicWidgetProvider.ACTION_STOP
            }
            putExtra(MusicWidgetProvider.EXTRA_SONG_NAME, currentTrack?.let{"${it.author} - ${it.name}"})
        })
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    inner class ServiceBinder : Binder(){
        val service: PlayerService
            get() = this@PlayerService
    }
}