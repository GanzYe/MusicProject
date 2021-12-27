package com.mark.musicproject

import com.mark.musicproject.db.models.TrackItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TrackRepo {

    private val db = MusicApplication.instance.db

    fun getTracks(): Flow<List<TrackItem>>{
        return db.trackDao().getTracks()
    }

    suspend fun addTrack(item: TrackItem){
        withContext(Dispatchers.IO){
            db.trackDao().addTrack(item)
        }
    }

    suspend fun addTrack(items: List<TrackItem>){
        withContext(Dispatchers.IO){
            db.trackDao().addTrack(items)
        }
    }
}