package spontaniius.ui.find_event

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import spontaniius.R
import org.json.JSONObject
import kotlin.math.roundToInt


class EventFindAdapter(private val myDataset: ArrayList<EventTile>) :
    RecyclerView.Adapter<EventFindAdapter.EventCardViewHolder>() {



     var EventTileList: ArrayList<EventTile>? = myDataset

    class EventCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImageView: ImageView
        var mTextView1: TextView
        var mTextView2: TextView
        var mTextViewDistance: TextView
        var mTextViewTime: TextView
        var direction_button: ImageView
        var details: RelativeLayout
        lateinit var eventid: String

        init {
            mImageView = itemView.findViewById(R.id.imageView)
            mTextView1 = itemView.findViewById(R.id.textView)
            mTextView2 = itemView.findViewById(R.id.textView2)
            mTextViewDistance = itemView.findViewById(R.id.textView3)
            mTextViewTime = itemView.findViewById(R.id.textView4)
            direction_button = itemView.findViewById(R.id.directionButton)
            details=itemView.findViewById(R.id.details)

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
        holder.mTextView1.setText(currentItem.title)
        holder.mTextView2.setText(currentItem.description)
        holder.eventid=currentItem.eventId


        var distance = currentItem.distance.toDoubleOrNull()
        distance = distance?.div(100)
        if (distance != null) {
            distance = distance.roundToInt().toDouble()
        }
        distance = distance?.div(10)


        var locationpointJSON = JSONObject(currentItem.location)
        var locationPoint = locationpointJSON.get("x").toString() + ", "+locationpointJSON.get("y").toString()

        holder.mTextViewDistance.setText(distance.toString() + " km")
        holder.mTextViewTime.setText(currentItem.time_started)
        holder.direction_button.setOnClickListener{

            val url = "https://www.google.com/maps/dir/?api=1&destination=${locationPoint}"
            startActivity(holder.direction_button.context, Intent(Intent.ACTION_VIEW, Uri.parse(url)),null)
        }


    }

    override fun getItemCount(): Int {
        return EventTileList?.size!!
    }

}