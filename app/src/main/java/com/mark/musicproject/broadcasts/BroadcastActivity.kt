package com.mark.musicproject.broadcasts

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mark.musicproject.BuildConfig
import com.mark.musicproject.databinding.ActivityBroadcastsBinding

class BroadcastActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBroadcastsBinding

    private val broadcastReceiverAnon = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(context, "Local broadcast", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBroadcastsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonIntentWithAction.setOnClickListener {
            sendBroadcast(Intent(MyBroadcastReceiver.ACTION_CUSTOM), Manifest.permission.RECEIVE_BOOT_COMPLETED)
        }

        binding.buttonIntentWithClass.setOnClickListener {
            sendBroadcast(Intent(this, MyBroadcastReceiver::class.java).apply {
                action = MyBroadcastReceiver.ACTION_CUSTOM
            })
        }

        binding.buttonLocalAction.setOnClickListener {
            sendBroadcast(Intent(MyBroadcastReceiver.ACTION_CUSTOM))
        }

        registerReceiver(broadcastReceiverAnon, IntentFilter().apply {
            addAction(MyBroadcastReceiver.ACTION_CUSTOM)
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        unregisterReceiver(broadcastReceiverAnon)
    }

}