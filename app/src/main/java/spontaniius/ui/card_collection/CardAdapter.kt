package spontaniius.ui.card_collection

import spontaniius.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView


class CardAdapter(books: List<UserCard>) :
    BaseAdapter() {
    private var userCards: List<UserCard> = books

    // 2
    override fun getCount(): Int {
        return userCards.size
    }

    // 3
    override fun getItemId(position: Int): Long {
        return 0
    }

    // 4
    override fun getItem(position: Int): Any? {
        return null
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val userCard: UserCard = userCards.get(position)
        val inflater = parent?.context?.
        getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(spontaniius.R.layout.user_card, null)

        // Get the custom view widgets reference
        val userName = view.findViewById<TextView>(R.id.textview_user_name)
        val userGreeting = view.findViewById<TextView>(R.id.textview_card_greeting)
        val cardBackground = view.findViewById<ImageView>(R.id.card_background)



        // 4

        // 0 is the identifyer that there is no background
        if(userCard.getBackground()!=0){
            cardBackground.setImageResource(userCard.getBackground())
        }

        userName.text = (userCard.getCardOwnerName())

        // Might choose to add this in later, for now plan to keep greeting blank unless card is clicked on and expanded.
        userGreeting.text = userCard.getGreeting()

        return view!!
        // 2

    }
}