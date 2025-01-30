package spontaniius.ui.event_management

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.core.Amplify
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.firebase.ui.database.FirebaseListAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONArray
import org.json.JSONObject
import com.spontaniius.R
import spontaniius.di.VolleySingleton
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "event_id"
private const val ARG_PARAM2 = "is_event_owner"


/**
 * A simple [Fragment] subclass.
 * Use the [EventManagementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventManagementFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var listenerManageEvent: EventManagementFragment.OnEventManagementFragmentInteractionListener? = null



    lateinit var myRef: DatabaseReference


    lateinit var listOfMessages: ListView
    private var adapter: FirebaseListAdapter<ChatMessage>? = null

    lateinit var fab: FloatingActionButton
    lateinit var input: EditText
    lateinit var fragmentView: View
    lateinit var endEventButton: Button
    lateinit var add15MinsButton : Button
    lateinit var detailsToggleButton: Button
    lateinit var chatroomToggleButton:Button
    lateinit var joinFromDetailsButon:Button
    lateinit var getDirectionsButton: Button
    lateinit var chatRoomView: Group
    lateinit var detailsView: LinearLayout
    lateinit var eventMemberCards: JSONArray
    var manager = false

    private var googleMap: GoogleMap? = null
    lateinit var mapFragment: SupportMapFragment
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */


        this.googleMap = googleMap
        googleMap.setOnMapLongClickListener { latLng ->

        }
        populateDetails(fragmentView)
    }
    var viewingDetails = true


    companion object {
        fun newInstance(param1: String, param2: Boolean): EventManagementFragment {
            return EventManagementFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putBoolean(ARG_PARAM2, param2)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        fragmentView = inflater.inflate(R.layout.fragment_event_management, container, false)

        var args = arguments
        var eventID = args!!.getString(ARG_PARAM1, "0")
        manager = args!!.getBoolean(ARG_PARAM2, false)
        myRef = FirebaseDatabase.getInstance().getReference(eventID)

        listOfMessages = fragmentView?.findViewById<View>(R.id.list_of_messages) as ListView
        fab = fragmentView?.findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            sendMessage()
        }

        if(manager){ // if you created this event you have control options
            endEventButton = fragmentView?.findViewById<View>(R.id.endButton) as Button
            endEventButton.setOnClickListener{
                sayGoodbye()
            }

            add15MinsButton = fragmentView?.findViewById<View>(R.id.addButton) as Button
            add15MinsButton.setOnClickListener{
                listenerManageEvent?.add15Mins()
            }
            viewingDetails=false

        }
        else // if you're joining somebody else's event
        {
            endEventButton = fragmentView?.findViewById<View>(R.id.endButton) as Button
            endEventButton.text = "Exit Event"
            endEventButton.setOnClickListener{
                listenerManageEvent?.exitEvent()
            }

            add15MinsButton = fragmentView?.findViewById<View>(R.id.addButton) as Button
            add15MinsButton.text = "Join in"
            add15MinsButton.setOnClickListener{
                joinIn()
                chatDetailsToggle()
            }
        }

        // Both joinees and mangers go through here to initialize some details
        detailsToggleButton = fragmentView?.findViewById<View>(R.id.detailsButton) as Button
        detailsToggleButton.setOnClickListener{
            chatDetailsToggle()
        }
        input = fragmentView?.findViewById<View>(R.id.input) as EditText
        chatRoomView = fragmentView?.findViewById(R.id.chatroomView) as Group
        detailsView = fragmentView?.findViewById(R.id.detailsView) as LinearLayout



        if(viewingDetails){ // We start event managers at chatroom, and event joinees to event details
            setGroupVisibility(fragmentView, chatRoomView, GONE)
            detailsView.visibility=VISIBLE
            detailsToggleButton.text = "Event Chat"

          //  setGroupVisibility(fragmentView,chatRoomView, VISIBLE)
        }else{
            setGroupVisibility(fragmentView, chatRoomView, VISIBLE)
            detailsView.visibility= GONE
            detailsToggleButton.text = "Event Details"

        }
        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment?.getMapAsync(callback)
        // Load chat room contents

        displayChatMessages();


        chatroomToggleButton = fragmentView?.findViewById<View>(R.id.chatroomToggle) as Button
        chatroomToggleButton.setOnClickListener{
            chatDetailsToggle()
        }
        joinFromDetailsButon = fragmentView?.findViewById<View>(R.id.chatroomAndJoin) as Button
        if(manager){
            joinFromDetailsButon.visibility= GONE
        }
        joinFromDetailsButon.setOnClickListener{
            joinIn()
            chatDetailsToggle()
        }

        // makes it so you have your own card already collected.

        var ownCardJsonArray = JSONArray()
        ownCardJsonArray.put(0, listenerManageEvent!!.getCurrentUserAttributes()!!.get("cardid"))
        eventMemberCards = ownCardJsonArray
        return fragmentView
    }

    fun joinIn(){
        sendCardGetCards() // Adds card to event register

        if(input.text.toString()==""){
            input.setText("Hey, I'm coming to join in")
            sendMessage()
        }
    }
    fun chatDetailsToggle(){

        viewingDetails = !viewingDetails  // We flip the logical statement
        if(viewingDetails){ // We start event managers at chatroom, and event joinees to event details
            setGroupVisibility(fragmentView, chatRoomView, GONE)
            detailsView.visibility=VISIBLE
            detailsToggleButton.text = "Event Chat"
            eventUpdate()

            //  setGroupVisibility(fragmentView,chatRoomView, VISIBLE)
        }else{
            setGroupVisibility(fragmentView, chatRoomView, VISIBLE)
            detailsView.visibility= GONE
            detailsToggleButton.text = "Event Details"

        }
    }

    private fun displayChatMessages() {

        adapter = object : FirebaseListAdapter<ChatMessage>(
            activity, ChatMessage::class.java,
            R.layout.message, myRef
        ) {
            override fun populateView(v: View, model: ChatMessage, position: Int) {
                // Get references to the views of message.xml
                val messageText = v.findViewById<View>(R.id.message_text) as TextView


                val messageUser = v.findViewById<View>(R.id.message_user) as TextView
                val messageTime = v.findViewById<View>(R.id.message_time) as TextView

                // Set their text
                messageText.text = model.messageText

                // A few select phrases that accompany app actions trigger a refreshing of data.
                if(model.messageText.toString().contains("Hey, I'm coming to join in")||model.messageText.toString().contains("Hope everyone had a good time, this event has ended")){
                    eventUpdate()
                }
                messageUser.text = model.messageUser

                // Format the date before showing it
                messageTime.setText(
                    convertLongToTime(model.messageTime)
                )

            }
        }
        listOfMessages.adapter = adapter;
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EventManagementFragment.OnEventManagementFragmentInteractionListener) {
            listenerManageEvent = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    fun sendCardGetCards(){

        var userAttributes: List<AuthUserAttribute?>? = null
        val cardExchangeDetails = JSONObject()
        val format = SimpleDateFormat("yyyy.MM.dd")
        var currDate = format.format(Calendar.getInstance().time)

        var aboutUser = listenerManageEvent?.getCurrentUserAttributes()

        var eventID = requireArguments().getString(ARG_PARAM1)
        cardExchangeDetails.put("eventid", eventID)
        cardExchangeDetails.put("cardid", aboutUser?.getString("cardid"))
        cardExchangeDetails.put("userid", aboutUser?.getString("userid"))
        cardExchangeDetails.put("meetingdate", currDate)

        val url = "https://1outrf3pp4.execute-api.us-west-2.amazonaws.com/default/joinEvent"
        val getLocationRequest = JsonObjectRequest(
            Request.Method.POST, url, cardExchangeDetails,
            { response ->
                val JSONResponse = JSONObject(response.toString())

                // We keep track of what cards are available when you join the event
                eventMemberCards = JSONResponse.get("cardids") as JSONArray


                alertCardReceived()
                joinFromDetailsButon.visibility= GONE
                add15MinsButton.visibility= GONE

            },
            { error ->
                error.printStackTrace()
            }

        )
        val queue = this?.let { this.context?.let { it1 -> VolleySingleton.getInstance(it1).requestQueue } }
        queue?.add(getLocationRequest)

    }

    // Meant to alert users when event has ended
    private fun sayGoodbye(){
        input.setText("Hope everyone had a good time, this event has ended")
        sendMessage()
        listenerManageEvent?.endEvent()
    }



    private fun sendMessage(){

        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        myRef
            .push()
            .setValue(
                ChatMessage(
                    input.text.toString(),
                    listenerManageEvent?.getCurrentUserAttributes()?.get("name").toString()
                )
            )

        // Clear the input
        input.setText("")

    }
    private fun setGroupVisibility(layout: View, group: Group, visibility: Int) {
        val refIds = group.referencedIds
        for (id in refIds) {
            layout.findViewById<View>(id).visibility = visibility
        }
    }

    // Basically fills the details screen
    private fun populateDetails(fragmentView: View) {
        var currentEvent = listenerManageEvent?.whatIsCurrentEvent()
        var eventTitle = fragmentView?.findViewById<View>(R.id.event_title) as TextView
        var eventDescription = fragmentView?.findViewById<View>(R.id.event_description) as TextView
        var eventStarts = fragmentView?.findViewById<View>(R.id.event_started) as TextView
        var eventEnds = fragmentView?.findViewById<View>(R.id.event_ends) as TextView

        eventTitle.text = currentEvent?.get("eventtitle").toString()
        eventDescription.text = currentEvent?.get("eventtext").toString()

        val endTimeString = currentEvent?.get("eventends")
        val startTimeString = currentEvent?.get("eventstarts")





        var endTime = ZonedDateTime.parse(
            endTimeString as CharSequence?, DateTimeFormatter.ofPattern(
                "yyyy-[M][MM]-[dd][d]['T'][ ][HH][H]:[mm][m]:[ss][s][.][SSSSSS][SSSSS][SSSS][SSS][XXX][XX][X][z]"
            )
        )

        var startTime = ZonedDateTime.parse(
            startTimeString as CharSequence?, DateTimeFormatter.ofPattern(
                "yyyy-[M][MM]-[dd][d]['T'][ ][HH][H]:[mm][m]:[ss][s][.][SSSSSS][SSSSS][SSSS][SSS][XXX][XX][X][z]" // Pattern is meant to be robust to any possible changes in database
            )
        )

        var dst = Calendar.getInstance().timeZone.dstSavings // Checks to see if daylight savings

        // Because postgres doesn't store timestamp we need to offset to local time. Currently manager keeps data from event creation, whereas fields are populated by api
        // For event joinees

        if(!manager){
            startTime = startTime.plusHours((Calendar.getInstance().timeZone.rawOffset.toLong() + dst) / 3600000)
            endTime = endTime.plusHours((Calendar.getInstance().timeZone.rawOffset.toLong() + dst) / 3600000)
        }



        var localEndTime = endTime.toLocalTime()
        var localStartTime = startTime.toLocalTime()


        eventStarts.text = LocalTime.parse(localStartTime.toString(),
            DateTimeFormatter.ofPattern("HH:mm")).format(
            DateTimeFormatter.ofPattern("hh:mm a"))
        eventEnds.text =
            LocalTime.parse(localEndTime.toString(), DateTimeFormatter.ofPattern("HH:mm")).format(
                DateTimeFormatter.ofPattern("hh:mm a"))


        // There's slight differences between how street address is stored in local object vs when retrieved from database, this handles that differece.

         var location: LatLng? = null
        if(manager) {
            val latlong = currentEvent?.get("streetaddress").toString().split(",".toRegex()).toTypedArray()
            val latitude = latlong[0].toDouble()
            val longitude = latlong[1].toDouble()
            location = LatLng(latitude, longitude)
        }else{
            val latlong = JSONObject(currentEvent?.get("streetaddress").toString())
            location = LatLng(latlong.getDouble("x"), latlong.getDouble("y"))
        }

        location.let {
            MarkerOptions()
                .position(it)
                .title(eventTitle.text.toString())
        }.let {
            googleMap?.addMarker(
                it
                // .icon(BitmapDescriptorFactory.fromResource(R.drawable.wave)
            )
        }

        if (::mapFragment.isInitialized) {

            val zoomLevel = 15.0f

            if (googleMap != null) {
                googleMap!!.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap!!.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap!!.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        location,
                        zoomLevel
                    )
                )
            }
        }

        getDirectionsButton = fragmentView?.findViewById<View>(R.id.directions) as Button
        getDirectionsButton.setOnClickListener{
            val url = "https://www.google.com/maps/dir/?api=1&destination=${currentEvent?.get("streetaddress")}"
            this?.context?.let { it1 ->
                ContextCompat.startActivity(
                    it1,
                    Intent(Intent.ACTION_VIEW, Uri.parse(url)),
                    null
                )
            }
        }

    }

    // Refreshes event details for event in Event Management Section
    fun eventUpdate(){
        var currEvent = listenerManageEvent?.whatIsCurrentEvent()
        val url = "https://i3ykarzwd5.execute-api.us-west-2.amazonaws.com/default/GetEventFromId?eventid="+requireArguments().getString(ARG_PARAM1);
        val getEventDetailsRequest = StringRequest(Request.Method.GET, url,
            { response ->
                val JSONResponse = JSONObject(response.toString())
                val potentialCards = JSONResponse.get("cardids") as JSONArray

                // Because you can't remove cards from an event, the number can only A) stay the same, so no new cards or B) increase meaning check and add missing cards
                if (eventMemberCards.length() != potentialCards.length()) {

                    var newCards = "array["
                    var commaBefore = false
                    // If there is a difference then we iterate through cards and find out what doesn't match
                    for (i in 0 until potentialCards.length()) {
                        val card  = potentialCards.get(i)
                        val new = true
                        for (j in 0 until eventMemberCards.length()){
                            val oldCard  = potentialCards.get(j)
                            if(oldCard.equals(card)) {
                                val new = false
                            }
                        }
                        if(new){
                            // We add the card id to the insertion string if its new
                            if (commaBefore == false){
                                commaBefore = true
                            }else{
                                newCards += ","
                            }
                            newCards += card.toString()
                        }
                    }
                    newCards +="]"

                    val format = SimpleDateFormat("yyyy.MM.dd")
                    var currDate = format.format(Calendar.getInstance().time)
                    var cardUpdateDetails = JSONObject()
                    var aboutUser = listenerManageEvent?.getCurrentUserAttributes()

                    cardUpdateDetails.put("userid", aboutUser?.getString("userid"))
                    cardUpdateDetails.put("cardids", newCards)
                    cardUpdateDetails.put("meetingdate", currDate)


                    var url = "https://0cfxpfaiy7.execute-api.us-west-2.amazonaws.com/default/addCardsToCollection"
                    val updateCardCollectionRequest = JsonObjectRequest(
                        Request.Method.POST, url, cardUpdateDetails,
                        { response ->
                            alertCardReceived()
                        },
                        { error ->
                            error.printStackTrace()
                        })

                    val queue = this?.let { this.context?.let { it1 -> VolleySingleton.getInstance(it1).requestQueue } }
                    queue?.add(updateCardCollectionRequest)
                }
                // We keep track of what cards are available when you join the event

            },
            { error ->
                error.printStackTrace()
            }

        )
        val queue = this?.let { this.context?.let { it1 -> VolleySingleton.getInstance(it1).requestQueue } }
        queue?.add(getEventDetailsRequest)

    }

    fun alertCardReceived(){
        //#TODO Update In future

    }


    interface OnEventManagementFragmentInteractionListener {
        fun endEvent()
        fun add15Mins()
        fun exitEvent()
        fun getEventID():Int
        fun whatIsCurrentEvent():JSONObject
        fun getCurrentUserAttributes(): JSONObject?
    }
}