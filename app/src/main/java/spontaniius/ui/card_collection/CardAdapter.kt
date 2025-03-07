package spontaniius.ui.card_collection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.spontaniius.R
import spontaniius.domain.models.Card

class CardAdapter(
    private val context: Context,
    private val userCards: List<Card>
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
        if (userCard.background != 0) {
            userCard.background?.let { viewHolder.cardBackground.setImageResource(it) }
        } else {
            viewHolder.cardBackground.setImageResource(R.drawable.card_gold) // Optional default background
        }

        // Set user name and greeting
        viewHolder.userName.text = userCard.name
        viewHolder.userGreeting.text = userCard.greeting ?: ""

        return view
    }

    private class ViewHolder(view: View) {
        val userName: TextView = view.findViewById(R.id.textview_user_name)
        val userGreeting: TextView = view.findViewById(R.id.textview_card_greeting)
        val cardBackground: ImageView = view.findViewById(R.id.card_background)
    }
}
