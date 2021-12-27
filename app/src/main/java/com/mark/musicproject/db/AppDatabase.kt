package com.mark.musicproject.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mark.musicproject.db.daos.TrackDao
import com.mark.musicproject.db.models.Playlist
import com.mark.musicproject.db.models.TrackItem
import com.mark.musicproject.db.relations.TrackPlaylistCrossRef

@Database(entities = [TrackItem::class,Playlist::class,TrackPlaylistCrossRef::class],version = 2)
abstract class AppDatabase: RoomDatabase() {

    abstract fun trackDao():TrackDao

}