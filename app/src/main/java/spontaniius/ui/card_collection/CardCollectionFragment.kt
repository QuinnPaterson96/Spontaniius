package spontaniius.ui.card_collection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.domain.models.Card


@AndroidEntryPoint
class CardCollectionFragment : Fragment() {

    private val viewModel: CardCollectionViewModel by viewModels()
    private lateinit var selectedCardBackground: ImageView
    private lateinit var selectedCardName: TextView
    private lateinit var selectedCardGreeting: TextView
    private lateinit var cardOptionsMenu: TextView
    private lateinit var gridView: GridView

    private var selectedCardId: String? = null
    private val userCardCollection = ArrayList<Card>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_card_collection, container, false)

        // Initialize UI elements
        selectedCardBackground = view.findViewById(R.id.selected_card_background)
        selectedCardGreeting = view.findViewById(R.id.selected_card_greeting)
        selectedCardName = view.findViewById(R.id.selected_card_user_name)
        cardOptionsMenu = view.findViewById(R.id.card_user_menu_button)
        gridView = view.findViewById(R.id.gridview)

        // Load user card collections

        // Observe LiveData updates
        setupObservers()
        viewModel.loadUserCardCollections()


        return view
    }

    private fun setupObservers() {
        // Observe user cards
        viewModel.userCards.observe(viewLifecycleOwner, Observer { cards ->
            userCardCollection.clear()
            userCardCollection.addAll(cards)
            updateUI(cards)
        })
    }

    private fun updateUI(cards: List<Card>) {
        if (cards.isEmpty()) {
            selectedCardName.text = getString(R.string.welcome_to_spontaniius)
            cardOptionsMenu.visibility = View.GONE
        } else {
            // Set initial selected card
            val selectedCard = cards.first()

            selectedCard.background?.let { selectedCardBackground.setImageResource(it.toInt()) }
            selectedCardGreeting.text = selectedCard.greeting
            selectedCardName.text = selectedCard.name
            selectedCardId = selectedCard.id.toString()
        }

        // Set up GridView adapter
        val cardAdapter = CardAdapter(requireContext(), userCardCollection)
        gridView.adapter = cardAdapter

        // Handle card selection
        gridView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if (position % 3 != 0) { // Skip date separator cards
                val selectedCard = userCardCollection[position]
                selectedCard.background?.let { selectedCardBackground.setImageResource(it.toInt()) }
                selectedCardGreeting.text = selectedCard.greeting
                selectedCardName.text = selectedCard.name
                selectedCardId = selectedCard.id.toString()
            }
        }

        // Setup options menu (report user)
        setupCardOptionsMenu()
    }

    private fun setupCardOptionsMenu() {
        cardOptionsMenu.setOnClickListener {
            val popup = PopupMenu(requireContext(), cardOptionsMenu)
            popup.menuInflater.inflate(R.menu.user_card_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.report_user -> {
                        val action = CardCollectionFragmentDirections
                            .actionCardCollectionFragmentToReportUserFragment(selectedCardId!!.toInt())

                        findNavController().navigate(action)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }
}
