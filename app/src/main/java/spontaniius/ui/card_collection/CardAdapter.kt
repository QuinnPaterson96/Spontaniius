package spontaniius.ui.card_collection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.spontaniius.R
import spontaniius.domain.models.Card

class CardAdapter(
    private val context: Context,
    private val userCards: List<Card>,
    private var selectedCardIndex: Int = -1  // Keep track of the selected card
) : BaseAdapter() {

    override fun getCount(): Int = userCards.size

    override fun getItem(position: Int): Card = userCards[position]

    override fun getItemId(position: Int): Long = userCards[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val view: View

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.user_card, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val userCard = getItem(position)

        // Set card background
        userCard.background?.let { viewHolder.cardBackground.setImageResource(it.toInt()) }

        // Set user name
        viewHolder.userName.text = userCard.name

        // Highlight selected card
        if (position == selectedCardIndex) {
            viewHolder.cardContainer.setCardBackgroundColor(ContextCompat.getColor(context, R.color.highlight_orange))
            viewHolder.cardContainer.cardElevation = 10f
        } else {
            viewHolder.cardContainer.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            viewHolder.cardContainer.cardElevation = 6f
        }

        return view
    }

    fun updateSelectedCard(newSelectedIndex: Int) {
        selectedCardIndex = newSelectedIndex
        notifyDataSetChanged() // Refresh adapter to update UI
    }

    private class ViewHolder(view: View) {
        val cardContainer: CardView = view.findViewById(R.id.card_container)
        val userName: TextView = view.findViewById(R.id.textview_user_name)
        val cardBackground: ImageView = view.findViewById(R.id.card_background)
    }
}
