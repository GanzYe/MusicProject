package com.mark.musicproject

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.mark.musicproject.databinding.ActivityTrackAddBinding

class TrackAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrackAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrackAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAdd.setOnClickListener {
            addTrack()
        }
    }

    private fun addTrack() {
        if(binding.editAuthor.length() <= 0 || binding.editName.length() <= 0 || binding.editUrl.length() <= 0){
            return
        }

        WorkManager.getInstance(this).let { manager ->
            val request = OneTimeWorkRequestBuilder<MusicDownloadWorker>()
                .setInputData(workDataOf(
                    MusicDownloadWorker.DATA_FILENAME to "${binding.editAuthor.text} - ${binding.editName.text}.mp3",
                    MusicDownloadWorker.DATA_URL to binding.editUrl.text.toString()
                ))
                .build()

            manager.enqueueUniqueWork("MusicDownload", ExistingWorkPolicy.APPEND, request)
            manager.getWorkInfoByIdLiveData(request.id).observe(this, {
                binding.progress.progress = it.progress.getInt(MusicDownloadWorker.DATA_PROGRESS, binding.progress.progress)
                if(it.state == WorkInfo.State.SUCCEEDED)
                    finish()
                else if (it.state == WorkInfo.State.FAILED){
                    Toast.makeText(this, "Cannot load resource", Toast.LENGTH_LONG).show()
                    binding.editUrl.setText("")
                    binding.buttonAdd.visibility = View.VISIBLE
                }
            })
            binding.progress.visibility = View.VISIBLE
            binding.buttonAdd.visibility = View.GONE
        }
    }

}