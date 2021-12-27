package com.mark.musicproject.broadcasts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.mark.musicproject.BuildConfig
import java.lang.StringBuilder

class MyBroadcastReceiver : BroadcastReceiver() {

    companion object{
        const val ACTION_CUSTOM = "${BuildConfig.APPLICATION_ID}.ACTION_CUSTOM"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        StringBuilder().apply {
            append("Action: ${intent?.action}\n")
            append("URI: ${intent?.toUri(Intent.URI_INTENT_SCHEME)}")
            toString().also { log ->
                Log.d("MyBroadcastReceiver", log)
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }

        val pendingResult = goAsync()
        pendingResult.finish()

    return
    }

}