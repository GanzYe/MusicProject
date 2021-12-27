package com.mark.musicproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mark.musicproject.databinding.ItemTrackBinding
import com.mark.musicproject.db.models.TrackItem

class TrackItemsAdapter(val items: ArrayList<TrackItem>) : RecyclerView.Adapter<TrackItemsAdapter.ItemViewHolder>() {

    var listener: OnClickListener? = null

    var prevPlayed: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(ItemTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.binding.textAuthor.text = item.author
        holder.binding.textName.text = item.name
        holder.binding.root.setOnClickListener {
            listener?.onClick(item, position)
        }
        holder.binding.imagePlay.visibility = if(item.isSelected)
            View.VISIBLE
        else
            View.GONE
        holder.binding.imagePlay.isSelected = item.isPlaying
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ItemViewHolder(val binding: ItemTrackBinding) : RecyclerView.ViewHolder(binding.root)

    fun interface OnClickListener{
        fun onClick(item: TrackItem, position: Int)
    }
}