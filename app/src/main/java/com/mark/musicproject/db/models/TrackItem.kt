package com.mark.musicproject.db.models

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class TrackItem(
    @ColumnInfo(index = true)
    val author: String,
    @ColumnInfo(index = true)
    val name: String,
    val uriString: String,
){
    @PrimaryKey
    var id:String = "$author$name"

    @Ignore
    val uri:Uri = Uri.parse(uriString)
    @Ignore
    var isPlaying: Boolean =false
    @Ignore
    var isSelected: Boolean = false
}
