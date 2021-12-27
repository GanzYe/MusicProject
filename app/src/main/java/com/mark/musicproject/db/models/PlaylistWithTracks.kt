package com.mark.musicproject.db.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.mark.musicproject.db.relations.TrackPlaylistCrossRef

data class PlaylistWithTracks (
    @Embedded
    val  playlist: Playlist,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(value = TrackPlaylistCrossRef::class,
                parentColumn = "playlistId",
                entityColumn = "trackId",
        )
    )
    val tracks:List<TrackItem>
)