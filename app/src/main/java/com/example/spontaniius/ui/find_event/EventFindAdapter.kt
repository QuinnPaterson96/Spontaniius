package com.example.spontaniius.ui.find_event

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spontaniius.R


class EventFindAdapter(private val myDataset: ArrayList<EventTile>) :
    RecyclerView.Adapter<EventFindAdapter.EventCardViewHolder>() {



     var EventTileList: ArrayList<EventTile>? = myDataset

    class EventCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImageView: ImageView
        var mTextView1: TextView
        var mTextView2: TextView

        init {
            mImageView = itemView.findViewById(R.id.imageView)
            mTextView1 = itemView.findViewById(R.id.textView)
            mTextView2 = itemView.findViewById(R.id.textView2)
        }
    }

    fun ExampleAdapter(exampleList: ArrayList<EventTile>?) {
        EventTileList = exampleList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventCardViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.event_tile, parent, false)
        return EventCardViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventCardViewHolder, position: Int) {
        val currentItem: EventTile = EventTileList!![position]
        holder.mImageView.setImageResource(currentItem.getImageResource())
        holder.mTextView1.setText(currentItem.getDescription())
        holder.mTextView2.setText(currentItem.getDistance_and_time_started())
    }

    override fun getItemCount(): Int {
        return EventTileList?.size!!
    }

}