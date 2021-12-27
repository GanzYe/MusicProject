package com.mark.musicproject

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.android.exoplayer2.extractor.mp4.Track
import com.mark.musicproject.db.models.TrackItem
import kotlinx.coroutines.*
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.net.URL
import kotlin.coroutines.CoroutineContext

class MusicDownloadWorker(applicationContext: Context, params: WorkerParameters)
    : CoroutineWorker(applicationContext, params) {

    companion object{
        const val DATA_URL = "url"
        const val DATA_FILENAME = "name_of_file"

        const val DATA_PROGRESS = "progress"
        const val RESULT_URI = "result_uri"
    }

    override suspend fun doWork(): Result {
        if(!inputData.hasKeyWithValueOfType(DATA_URL, String::class.java))
            return Result.failure()
        if(!inputData.hasKeyWithValueOfType(DATA_FILENAME, String::class.java))
            return Result.failure()

        val urlString = inputData.getString(DATA_URL) ?: return Result.failure()
        val filename = inputData.getString(DATA_FILENAME) ?: return Result.failure()

        try {
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.connect()

            val length = connection.contentLength.toDouble()

            val file =
                File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_MUSIC), filename)

            BufferedInputStream(url.openStream(), DEFAULT_BUFFER_SIZE).use { input ->
                file.outputStream().use { output ->
                    setProgress(workDataOf(
                        DATA_PROGRESS to 0
                    ))
                    val data = ByteArray(1024)
                    var total = 0
                    var count = input.read(data)
                    coroutineScope {
                        launch {
                            while (count >= 0) {
                                total += count
                                output.write(data, 0, count)
                                count = input.read(data)
                            }
                        }
                        launch {
                            var progress: Int
                            while (count >= 0){
                                try {
                                    progress = ((total / length) * 100).toInt()
                                    setProgress(workDataOf(Companion.DATA_PROGRESS to progress))
                                } catch (e: Exception){
                                    Log.e("Worker", e.message ?: "")
                                }
                                delay(2000)
                            }
                        }
                    }
                    output.flush()
                }
            }

            setProgress(workDataOf(DATA_PROGRESS to 100))

            val fileName = file.name.split(".").firstOrNull() ?: ""
            val fileNameSplit = fileName.split("-")

            TrackRepo().addTrack(
                TrackItem(
                    fileNameSplit.firstOrNull()?:"",
                    fileNameSplit.getOrNull(1)?:"",
                    Uri.fromFile(file).toString()
                )
            )

            return Result.success()
        } catch (e: IOException) {
            Log.e("MusicDownloadWorker", e.message ?: "")
            return Result.failure()
        }
    }

}