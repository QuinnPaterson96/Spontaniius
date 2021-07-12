package spontaniius.ui.event_management

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import com.firebase.ui.database.FirebaseListAdapter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import spontaniius.R
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "eventid"
private const val ARG_PARAM2 = "role"


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
    lateinit var chatRoomView: Group
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
    }
    var detailView = true


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
        var manager = args!!.getBoolean(ARG_PARAM2, false)
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
            detailView=false
        }else{ // if you're joining somebody else's event
            endEventButton = fragmentView?.findViewById<View>(R.id.endButton) as Button
            endEventButton.text = "Exit Event"
            endEventButton.setOnClickListener{
                listenerManageEvent?.exitEvent()
            }

            add15MinsButton = fragmentView?.findViewById<View>(R.id.addButton) as Button
            add15MinsButton.text = "Join in"
            add15MinsButton.setOnClickListener{
                sendCard() // Adds card to event register
                if(input.text.toString()==""){
                    input.setText("Hey, I'm coming to join in")
                    sendMessage()
                }
            }
        }
        detailsToggleButton = fragmentView?.findViewById<View>(R.id.detailsButton) as Button
        detailsToggleButton.setOnClickListener{

        }
        input = fragmentView?.findViewById<View>(R.id.input) as EditText
        chatRoomView = fragmentView?.findViewById(R.id.chatroomView) as Group


        if(detailView){ // We start event managers at chatroom, and event joinees to event details
            setGroupVisibility(fragmentView,chatRoomView, INVISIBLE)
          //  setGroupVisibility(fragmentView,chatRoomView, VISIBLE)
        }else{

        }
        // Load chat room contents
        displayChatMessages();
        return fragmentView
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
        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment?.getMapAsync(callback)
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

    fun sendCard(){

    }
    private fun sayGoodbye(){
        input.setText("Hope everyone had a good time, this event has ended")
        sendMessage()
        listenerManageEvent?.endEvent()
    }

    interface OnEventManagementFragmentInteractionListener {
        fun endEvent()
        fun add15Mins()
        fun exitEvent()
    }

    private fun sendMessage(){

        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        myRef
            .push()
            .setValue(
                ChatMessage(
                    input.text.toString(),
                    FirebaseAuth.getInstance()
                        .currentUser
                        ?.getDisplayName()
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

}