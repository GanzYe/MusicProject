package com.mark.musicproject.db.daos

import androidx.room.*
import com.mark.musicproject.db.models.PlaylistWithTracks
import com.mark.musicproject.db.models.TrackItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM trackitem")
    fun getTracks():Flow<List<TrackItem>>

    @Query("SELECT * FROM trackitem WHERE author =:author")
    fun getTracksByAuthor(author:String):Flow<List<TrackItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(track: TrackItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(tracks: List<TrackItem>)

    @Update
    suspend fun updateTrack(track:TrackItem)

    @Delete
    suspend fun deleteTrack(track:TrackItem)

    @Query("SElECT * FROM playlist")
    fun getPlaylist():Flow<List<PlaylistWithTracks>>
}