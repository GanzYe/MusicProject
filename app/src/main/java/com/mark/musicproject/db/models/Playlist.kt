package com.mark.musicproject.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Playlist(
    val name: String
){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}