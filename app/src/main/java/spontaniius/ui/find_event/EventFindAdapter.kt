package spontaniius.ui.find_event

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.spontaniius.R
import org.json.JSONObject
import spontaniius.domain.models.Event
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class EventFindAdapter(private var eventList: List<EventTile>) :
    RecyclerView.Adapter<EventFindAdapter.EventCardViewHolder>() {

    class EventCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mImageView: ImageView = itemView.findViewById(R.id.imageView)
        val mTextView1: TextView = itemView.findViewById(R.id.textView)
        val mTextView2: TextView = itemView.findViewById(R.id.textView2)
        val mTextViewDistance: TextView = itemView.findViewById(R.id.textView3)
        val mTextViewTime: TextView = itemView.findViewById(R.id.textView4)
        val directionButton: ImageView = itemView.findViewById(R.id.directionButton)
        val details: ConstraintLayout = itemView.findViewById(R.id.details)
        val timeRemainIndicator: Button = itemView.findViewById(R.id.time_indicator)

        var eventId: Int = 0
        lateinit var event: Event
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventCardViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.event_tile, parent, false)
        return EventCardViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventCardViewHolder, position: Int) {
        val currentItem = eventList[position]

        holder.mImageView.setImageResource(currentItem.imageResource)
        holder.mTextView1.text = currentItem.title
        holder.mTextView2.text = currentItem.description
        holder.eventId = currentItem.eventId
        holder.event = currentItem.event

        // Convert and format distance
        val formattedDistance = currentItem.distance.toDoubleOrNull()?.let {
            (it / 1000).roundToInt().toDouble()
        }?.toString() ?: "N/A"

        holder.mTextViewDistance.text = "$formattedDistance km"
        val eventStartTime = try {
            ZonedDateTime.parse(currentItem.event.startTime, DateTimeFormatter.ISO_DATE_TIME)
        } catch (e: Exception) {
            null
        }

        val isUpcoming = eventStartTime?.isAfter(ZonedDateTime.now()) ?: false

// Set the text and indicator color based on whether the event is upcoming
        holder.mTextViewTime.text = if (isUpcoming) {
            "Starts at ${eventStartTime?.toLocalTime()}"
        } else {
            "Ongoing"
        }

        holder.timeRemainIndicator.setBackgroundColor(
            if (isUpcoming)
                Color.parseColor("#9ffc58")  // Green for future events
            else
                Color.parseColor("#fafa9b")  // Yellow for ongoing events
        )

        // Handle directions
        holder.directionButton.setOnClickListener {
            val locationPoint = try {
                val locationJSON = JSONObject(currentItem.location)
                "${locationJSON.getDouble("x")},${locationJSON.getDouble("y")}"
            } catch (e: Exception) {
                null
            }

            locationPoint?.let {
                val url = "https://www.google.com/maps/dir/?api=1&destination=$it"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                holder.directionButton.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = eventList.size
}
