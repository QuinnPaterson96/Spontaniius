package spontaniius.ui.card_editing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import com.spontaniius.R
import spontaniius.ui.sign_up.SignUpFragmentArgs

@AndroidEntryPoint
class CardEditingFragment : Fragment() {

    private val viewModel: CardEditingViewModel by viewModels()
    private val args: CardEditingFragmentArgs by navArgs()
    private lateinit var greetingView: TextView
    private lateinit var selectedCardBackground: ImageView
    private lateinit var cardGreetingEdit: EditText
    private lateinit var loadingBar: ProgressBar
    private lateinit var userNameView: TextView

    private var backgroundID = 0
    private val cardBackgrounds = arrayOf(
        R.drawable.card_gold, R.drawable.card_bubbles, R.drawable.card_sunrise,
        R.drawable.card_ocean, R.drawable.card_rose, R.drawable.card_trees, R.drawable.card_circuit
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_card_editing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        greetingView = view.findViewById(R.id.selected_card_greeting)
        selectedCardBackground = view.findViewById(R.id.selected_card_background)
        loadingBar = view.findViewById(R.id.loading)
        cardGreetingEdit = view.findViewById(R.id.card_greeting_edit)


        userNameView = view.findViewById<TextView>(R.id.selected_card_user_name)
        selectedCardBackground.setImageResource(cardBackgrounds[0])
        backgroundID = cardBackgrounds[0]

        // Set up card selection buttons
        setupCardSelection(view)

        if (args.newUser) {
            view.findViewById<Button>(R.id.cancel_button).visibility = View.GONE
        }

        view.findViewById<Button>(R.id.cancel_button).setOnClickListener {
            findNavController().popBackStack()
        }

        view.findViewById<Button>(R.id.save_button).setOnClickListener {
            viewModel.createCard(cardGreetingEdit.text.toString(), backgroundID)
        }

        setupObservers()


        // Text change listener
        cardGreetingEdit.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                greetingView.text = s.toString()
            }
        })


        viewModel.getUserCardDetails()
        viewModel.getUserDetails()

    }

    fun setupObservers(){
        // Observe ViewModel state
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            loadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }

        viewModel.cardCreated.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().navigate(R.id.findEventFragment)
            }
        }
        viewModel.userCard.observe(viewLifecycleOwner){ card ->
            if (card!=null){
                if (card.greeting!=null){
                    cardGreetingEdit.setText(card.greeting)
                }
                selectedCardBackground.setImageResource(card.background.toInt())
            }
        }
        viewModel.userDetails.observe(viewLifecycleOwner){ user ->
            userNameView.text = user.name
        }
    }

    private fun setupCardSelection(view: View) {
        val buttons = arrayOf(
            view.findViewById<AppCompatImageButton>(R.id.card0),
            view.findViewById<AppCompatImageButton>(R.id.card1),
            view.findViewById<AppCompatImageButton>(R.id.card2),
            view.findViewById<AppCompatImageButton>(R.id.card3),
            view.findViewById<AppCompatImageButton>(R.id.card4),
            view.findViewById<AppCompatImageButton>(R.id.card5),
            view.findViewById<AppCompatImageButton>(R.id.card6)
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                backgroundID = cardBackgrounds[index]
                selectedCardBackground.setImageResource(backgroundID)
            }
        }
    }
}
