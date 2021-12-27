package com.mark.musicproject.services

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class MusicWidgetService: RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return MusicListRemoteViewsFactory(this.applicationContext,intent)
    }
}

class MusicListRemoteViewsFactory(private val context: Context, intent: Intent?):RemoteViewsService.RemoteViewsFactory{
    override fun onCreate() {
        TODO("Not yet implemented")
    }

    override fun onDataSetChanged() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }

    override fun getCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getViewAt(p0: Int): RemoteViews {
        TODO("Not yet implemented")
    }

    override fun getLoadingView(): RemoteViews {
        TODO("Not yet implemented")
    }

    override fun getViewTypeCount(): Int {
        TODO("Not yet implemented")
    }

    override fun getItemId(p0: Int): Long {
        TODO("Not yet implemented")
    }

    override fun hasStableIds(): Boolean {
        TODO("Not yet implemented")
    }

}