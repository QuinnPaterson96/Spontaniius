package spontaniius.ui.card_collection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.GONE
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.core.Amplify
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONObject
import spontaniius.R
import spontaniius.dependency_injection.VolleySingleton
import spontaniius.ui.event_management.EventManagementFragment
import spontaniius.ui.report_user.CARD_ID
import spontaniius.ui.report_user.ReportUserActivity
import spontaniius.ui.sign_up.*
import spontaniius.ui.user_menu.UserOptionsActivity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
lateinit var selectedCardBackground: ImageView
lateinit var selectedCardName: TextView
lateinit var selectedCardGreeting: TextView
lateinit var selectedCardId: String
lateinit var cardOptionsMenu:TextView
lateinit var gridView: GridView

/**
 * A simple [Fragment] subclass.
 * Use the [CardCollectionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CardCollectionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var listenerCardCollection:CardCollectionFragment.OnCardCollectionFragmentInteractionListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view: View = inflater.inflate(R.layout.fragment_card_collection, container, false)
        selectedCardBackground=view.findViewById(R.id.selected_card_background)
        selectedCardGreeting=view.findViewById(R.id.selected_card_greeting)
        selectedCardName=view.findViewById(R.id.selected_card_user_name)
        cardOptionsMenu = view.findViewById<TextView>(R.id.card_user_menu_button)
        gridView = view?.findViewById(R.id.gridview) as GridView

        setupCards()
      //  var userCardCollection= listOf<UserCard>()

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CardCollectionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CardCollectionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        fun newInstance() =
            CardCollectionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }





    fun setupCards(){
        val userCardCollection: ArrayList<UserCard> = ArrayList()


        // We start of by retrieving card collection from database using user's id
        val url =
            "https://17tbc8bm31.execute-api.us-west-2.amazonaws.com/default/GetCardCollectionDetails?userid=${Amplify.Auth.currentUser.userId}";
        val getEventsRequest = StringRequest(Request.Method.GET, url,
            { response ->

                val eventJSONArray = JSONArray(response.toString())
                var currentDate = ""

                if(eventJSONArray.length()==0){
                    selectedCardName.text="Welcome To Spontaniius"
                    cardOptionsMenu.visibility= View.GONE
                }

                for (i in 0 until eventJSONArray.length()) {
                    val event: JSONObject = eventJSONArray.getJSONObject(i)
                    val meetingdateString = event.get("meetingdate")

                    val meetingDate = ZonedDateTime.parse(
                        meetingdateString as CharSequence?, DateTimeFormatter.ofPattern(
                            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
                        )
                    )

                    var debugDate = meetingDate.toLocalDate()
                    var thisDate = debugDate



                    // This is the way we break up gridview by date, using fake tiles to take advantage of automatic formatting
                    if (thisDate.toString() != currentDate) {
                        currentDate =
                            thisDate.toString() // Update date, so as to clarify between sections


                        var emptyblocks =
                            (userCardCollection.size) % 3 // This finishes off current row with blanks to create some space between dates
                        if(emptyblocks!=0){
                            for (k in 0 until 3-emptyblocks) {
                                var blanks = UserCard(
                                    "",
                                    0,
                                    "",
                                    ""
                                )

                                userCardCollection.add(blanks)
                            }
                        }



                        // Now we add in the date card
                        var dateCard = UserCard(
                            currentDate,
                            0,
                            "",
                            ""
                        )

                        userCardCollection.add(dateCard)
                    }


                    // This is used to maintain a pattern of date-card-card or blank-card-card
                    if((userCardCollection.size) % 3==0){
                        var blanks = UserCard(
                            "",
                            0,
                            "",
                            ""
                        )

                        userCardCollection.add(blanks)
                    }




                    try {
                        var hangout = UserCard(
                            event.get("cardtext").toString(),
                            event.get("background").toString().toInt(),
                            event.get("cardid").toString(),
                            event.get("greeting").toString()
                        )

                        userCardCollection.add(hangout)
                    } finally {

                    }
                }
                if (userCardCollection.size != 0) {
                    var selectedCard =
                        userCardCollection.get(1) // We start at 1 because 0th position will be a date card
                    selectedCardBackground.setImageResource(selectedCard.getBackground())
                    selectedCardGreeting.text = selectedCard.getGreeting()
                    selectedCardName.text = selectedCard.getCardOwnerName()
                    selectedCardId= selectedCard.getCardUserID()!! // This is for reporting mechanism
                }

            },
            { error ->
                error.printStackTrace()

            }
        )
        val queue = this.context?.let { VolleySingleton.getInstance(it).requestQueue }
        queue?.add(getEventsRequest)





        val cardAdapter = this.context?.let { CardAdapter(userCardCollection) }
        gridView.adapter = cardAdapter
        gridView.onItemClickListener =
            OnItemClickListener { adapterView, view, position, l ->

                if(position%3!=0){
                    // We only want to change card if a card is collected
                    var selectedCard = userCardCollection.get(position) // We get the newly selected card
                    selectedCardBackground.setImageResource(selectedCard.getBackground())
                    selectedCardGreeting.text = selectedCard.getGreeting()
                    selectedCardName.text = selectedCard.getCardOwnerName()
                    selectedCardId= selectedCard.getCardUserID()!!
                }
            }




        cardOptionsMenu?.setOnClickListener {

            val popup = PopupMenu(this.context, cardOptionsMenu)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.user_card_menu, popup.menu)
            var currentContext = this.context
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {

                    return when (item.getItemId()) {
                        R.id.report_user -> {
                            val intentReportUser =
                                Intent(currentContext, ReportUserActivity::class.java).apply {
                                    putExtra(CARD_ID, selectedCardId)
                                }

                            startActivity(intentReportUser)
                            return true
                        }
                        else -> false
                    }
                }
            })
            popup.show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CardCollectionFragment.OnCardCollectionFragmentInteractionListener) {
            listenerCardCollection = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    interface OnCardCollectionFragmentInteractionListener {
        fun getCurrentUserAttributes():JSONObject
    }
}