package spontaniius.ui.event_management

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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
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
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.data.remote.models.JoinEventRequest
import spontaniius.domain.models.Event
import spontaniius.domain.models.User
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
@AndroidEntryPoint
class EventManagementFragment : Fragment() {
    private val viewModel: EventManagementViewModel by viewModels()
    lateinit var messagesRef: DatabaseReference
    var currentUser: User? = null

    lateinit var listOfMessages: RecyclerView
    private var chatAdapter: FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder>? = null

    lateinit var fab: FloatingActionButton
    lateinit var input: EditText
    lateinit var fragmentView: View
    lateinit var endEventButton: Button
    lateinit var add15MinsButton : Button
    lateinit var detailsToggleButton: Button
    lateinit var chatroomToggleButton:Button
    lateinit var joinFromDetailsButton:Button
    lateinit var getDirectionsButton: Button
    lateinit var chatRoomView: Group
    lateinit var detailsView: LinearLayout
    lateinit var eventMemberCards: JSONArray
    var manager = false
    var eventId: Int? = null
    private var googleMap: GoogleMap? = null
    lateinit var mapFragment: SupportMapFragment
    lateinit var currentEvent: Event
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
    ): View {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        fragmentView = inflater.inflate(R.layout.fragment_event_management, container, false)

        val eventId: Int = arguments?.getInt("eventId") ?: 0  // Default value if null


        messagesRef = FirebaseDatabase.getInstance().getReference(eventId.toString())

        listOfMessages = fragmentView.findViewById<View>(R.id.list_of_messages) as RecyclerView
        fab = fragmentView.findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            sendMessage()
        }

        if(manager){ // if you created this event you have control options
            endEventButton = fragmentView.findViewById<View>(R.id.endButton) as Button
            endEventButton.setOnClickListener{

            }

            add15MinsButton = fragmentView.findViewById<View>(R.id.addButton) as Button
            add15MinsButton.setOnClickListener{
                viewModel.add15Mins(eventId = currentEvent.eventId, currentEndTime = currentEvent.endTime)
            }
            viewingDetails=false

        }
        else // if you're joining somebody else's event
        {
            endEventButton = fragmentView.findViewById<View>(R.id.endButton) as Button
            endEventButton.text = getString(R.string.exit_event)
            endEventButton.setOnClickListener{
                findNavController().navigate(R.id.findEventFragment)

            }
            add15MinsButton = fragmentView.findViewById<View>(R.id.addButton) as Button
            add15MinsButton.text = getString(R.string.join_event)
            add15MinsButton.setOnClickListener{
                viewModel.joinEvent(eventId)
                chatDetailsToggle()
            }
        }

        // Both joinees and mangers go through here to initialize some details
        detailsToggleButton = fragmentView.findViewById<View>(R.id.detailsButton) as Button
        detailsToggleButton.setOnClickListener{
            chatDetailsToggle()
        }
        input = fragmentView.findViewById<View>(R.id.input) as EditText
        chatRoomView = fragmentView.findViewById<Group>(R.id.chatroomView)!!
        detailsView = fragmentView.findViewById<LinearLayout>(R.id.detailsView)!!



        if(viewingDetails){ // We start event managers at chatroom, and event joinees to event details
            setGroupVisibility(fragmentView, chatRoomView, GONE)
            detailsView.visibility=VISIBLE
            detailsToggleButton.text = getString(R.string.event_chat)

            //  setGroupVisibility(fragmentView,chatRoomView, VISIBLE)
        }else{
            setGroupVisibility(fragmentView, chatRoomView, VISIBLE)
            detailsView.visibility= GONE
            detailsToggleButton.text = getString(R.string.event_details)
        }

        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(callback)
        // Load chat room contents
        displayChatMessages();

        chatroomToggleButton = fragmentView.findViewById<View>(R.id.chatroomToggle) as Button
        chatroomToggleButton.setOnClickListener{
            chatDetailsToggle()
        }
        joinFromDetailsButton = fragmentView.findViewById<View>(R.id.chatroomAndJoin) as Button
        if(manager){
            joinFromDetailsButton.visibility= GONE
        }
        joinFromDetailsButton.setOnClickListener{
            viewModel.joinEvent(eventId)
            chatDetailsToggle()
        }

        // makes it so you have your own card already collected.

        var ownCardJsonArray = JSONArray()

        viewModel.userDetails.observe(viewLifecycleOwner){ user ->
            if (user != null) {
                currentUser = user
                ownCardJsonArray.put(0, user.cardId)
            }
        }

        viewModel.eventJoined.observe(viewLifecycleOwner){joined ->
            if(joined){
                joinFromDetailsButton.visibility = View.GONE
                add15MinsButton.visibility = View.GONE
            }
        }


        viewModel.eventDetails.observe(viewLifecycleOwner){ event ->
            if (event != null) {
                currentEvent = event
                populateDetails(fragmentView)

            }
        }

        return fragmentView
    }


    fun chatDetailsToggle(){

        viewingDetails = !viewingDetails  // We flip the logical statement
        if(viewingDetails){ // We start event managers at chatroom, and event joinees to event details
            setGroupVisibility(fragmentView, chatRoomView, GONE)
            detailsView.visibility=VISIBLE
            detailsToggleButton.text = getString(R.string.event_chat)
            eventUpdate(currentEvent.eventId)

            //  setGroupVisibility(fragmentView,chatRoomView, VISIBLE)
        }else{
            setGroupVisibility(fragmentView, chatRoomView, VISIBLE)
            detailsView.visibility= GONE
            detailsToggleButton.text = getString(R.string.event_details)

        }
    }

    private fun displayChatMessages() {

        val query = messagesRef.orderByChild("messageTime")

        // FirebaseRecyclerOptions (Needed for FirebaseRecyclerAdapter)
        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(query, ChatMessage::class.java)
            .build()

        // Initialize FirebaseRecyclerAdapter
        chatAdapter = object : FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.message, parent, false)
                return ChatViewHolder(view)
            }

            override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: ChatMessage) {
                holder.bind(model)
            }
        }

        // Set adapter
        listOfMessages.adapter = chatAdapter
    }

    private fun sendMessage(){

        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        messagesRef
            .push()
            .setValue(
                ChatMessage(
                    input.text.toString(),
                    currentUser?.name
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
        var eventTitle = fragmentView.findViewById<View>(R.id.event_title) as TextView
        var eventDescription = fragmentView.findViewById<View>(R.id.event_description) as TextView
        var eventStarts = fragmentView.findViewById<View>(R.id.event_started) as TextView
        var eventEnds = fragmentView.findViewById<View>(R.id.event_ends) as TextView

        eventTitle.text = currentEvent.title
        eventDescription.text = currentEvent.description

        val endTimeString = currentEvent.endTime
        val startTimeString = currentEvent.startTime

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
            val latlong = currentEvent.address
            val latitude = latlong[0].toDouble()
            val longitude = latlong[1].toDouble()
            location = LatLng(latitude, longitude)
        }else{
            val latlong = JSONObject(currentEvent.address)
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

        getDirectionsButton = fragmentView.findViewById<View>(R.id.directions) as Button
        getDirectionsButton.setOnClickListener {
            val streetAddress = currentEvent.address as? String
            val mapsUrl = viewModel.getGoogleMapsUrl(streetAddress)

            mapsUrl?.let {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                startActivity(intent)
            }
        }


    }

    // Refreshes event details for event in Event Management Section
    fun eventUpdate(event_id: Int){
        viewModel.loadEventDetails(event_id)
    }

    fun alertCardReceived(){
        //#TODO Update In future

    }

    // ViewHolder Class
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)
        private val messageUser: TextView = itemView.findViewById(R.id.message_user)
        private val messageTime: TextView = itemView.findViewById(R.id.message_time)

        fun bind(chatMessage: ChatMessage) {
            messageText.text = chatMessage.messageText
            messageUser.text = chatMessage.messageUser
            messageTime.text = convertLongToTime(chatMessage.messageTime)

            // If specific messages trigger an update
            if (chatMessage.messageText!!.contains("Hey, I'm coming to join in") ||
                chatMessage.messageText!!.contains("Hope everyone had a good time, this event has ended")
            ) {
                // #TODO Fix this later   eventUpdate()
            }

        }

        // Function to convert timestamp to readable format
        private fun convertLongToTime(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return format.format(date)
        }
    }
}



