package com.mark.musicproject.db.relations

import androidx.room.Entity

@Entity(primaryKeys = ["trackId","playlistId"])
data class TrackPlaylistCrossRef (
    val trackId:String,
    val playlistId:Int
)