package com.mark.musicproject

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.mark.musicproject.databinding.ActivityServiceBinding
import com.mark.musicproject.services.PlayerService

class ServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServiceBinding

    private var service: PlayerService? = null
    private var mBound: Boolean = false

    private val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            binder?.let {
                service = (binder as PlayerService.ServiceBinder).service
                mBound = true
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonStart.setOnClickListener {
            startService()
        }

        binding.buttonBind.setOnClickListener {
            bindToService()
        }

        binding.buttonUnbind.setOnClickListener {
            unbindFromService()
        }

        binding.buttonStop.setOnClickListener {
            stopService()
        }

        binding.buttonDownload.setOnClickListener {
            downloadFile()
        }
    }

    private fun downloadFile() {
        WorkManager.getInstance(applicationContext).let { manager ->
            val workRequest = OneTimeWorkRequestBuilder<MusicDownloadWorker>()
                .setInputData(workDataOf(
                    MusicDownloadWorker.DATA_URL to "https://sefon.pro/api/mp3_download/direct/222635/3vUCAEOC116j43by2jgjulRIaq7SqKUxbrBfRdtvogBr7zp9DEJVakprhVUQ3lHUm-fQUrXmgMUJqVQfkEDX4duOvnhUFnXcik8x972VlDt5DG3DBDXqt17jWODoPeCWHfFusOoW-hd7DtLE9s_W6ctEXzp-s9nD9hk/",
                    MusicDownloadWorker.DATA_FILENAME to "Король и шут - Кукла колдуна.mp3"
                ))
                .build()

            val process = manager.enqueueUniqueWork("MusicDownloadWorker", ExistingWorkPolicy.REPLACE, workRequest)
            manager.getWorkInfoByIdLiveData(workRequest.id).observe(this, {
                it?.let { info ->
                    binding.progress.progress = info.progress.getInt(MusicDownloadWorker.DATA_PROGRESS, binding.progress.progress)
                    if(info.state == WorkInfo.State.SUCCEEDED)
                        Toast.makeText(this, "Done", Toast.LENGTH_LONG).show()
                    else if(info.state == WorkInfo.State.FAILED)
                        Toast.makeText(this, "Something goes wrong", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun startService(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, PlayerService::class.java))
        } else{
            startService(Intent(this, PlayerService::class.java))
        }
    }

    private fun bindToService(){
        if(!mBound)
            bindService(Intent(this, PlayerService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindFromService(){
        if(mBound) {
            unbindService(serviceConnection)
            mBound = false
        }
    }

    private fun stopService(){
        stopService(Intent(this, PlayerService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mBound)
            unbindFromService()
    }

}