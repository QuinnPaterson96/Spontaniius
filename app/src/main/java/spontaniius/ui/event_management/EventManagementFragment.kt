package spontaniius.ui.event_management

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONArray
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.domain.models.Event
import spontaniius.domain.models.User
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
    var currentUser: User? = null
    lateinit var input: EditText
    lateinit var fragmentView: View
    lateinit var endEventButton: Button
    lateinit var add15MinsButton : Button
    lateinit var goToChatButton: Button
    lateinit var joinFromDetailsButton:Button
    lateinit var getDirectionsButton: Button
    var manager = false
    var eventId: Int? = null
    private var googleMap: GoogleMap? = null
    lateinit var mapFragment: SupportMapFragment
    lateinit var currentEvent: Event

    private val google_maps_callback = OnMapReadyCallback { googleMap ->
        this.googleMap = googleMap
        googleMap.setOnMapLongClickListener { latLng ->

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


// if you created this event you have control options
        endEventButton = fragmentView.findViewById<View>(R.id.endButton) as Button
        endEventButton.setOnClickListener{
            FirebaseMessaging.getInstance().unsubscribeFromTopic("event_${eventId}")
            viewModel.endEvent(eventId)
        }

        add15MinsButton = fragmentView.findViewById<View>(R.id.addButton) as Button
        add15MinsButton.setOnClickListener{
            viewModel.add15Mins(eventId = currentEvent.eventId, currentEndTime = currentEvent.endTime)
        }




        // Both joinees and mangers go through here to initialize some details
        goToChatButton = fragmentView.findViewById<View>(R.id.event_chat_button) as Button
        goToChatButton.setOnClickListener{
            goToEventChat()
        }


        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(google_maps_callback)

        // makes it so you have your own card already collected.

        var ownCardJsonArray = JSONArray()

        viewModel.userDetails.observe(viewLifecycleOwner){ user ->
            if (user != null) {
                currentUser = user
                ownCardJsonArray.put(0, user.cardId)
            }
        }

        viewModel.eventEnded.observe(viewLifecycleOwner){ended ->
            if(ended){
                findNavController().navigate(R.id.findEventFragment)
            }
        }


        viewModel.eventDetails.observe(viewLifecycleOwner){ event ->
            if (event != null) {
                currentEvent = event
                populateDetails(fragmentView)

            }
        }
        viewModel.loadEventDetails(eventId)
        FirebaseMessaging.getInstance().subscribeToTopic("event_${eventId}")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM_Debug", "Subscribed to event_$eventId notifications")
                } else {
                    Log.e("FCM_Debug", "Failed to subscribe to event_$eventId")
                }
            }

        return fragmentView
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


        var location: LatLng = LatLng(currentEvent.latitude, currentEvent.longitude)

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
            val mapsUrl = viewModel.getGoogleMapsUrl(lat = currentEvent.latitude, lng = currentEvent.longitude)

            mapsUrl?.let {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                startActivity(intent)
            }
        }
    }

    fun goToEventChat(){
        val action =  EventManagementFragmentDirections.actionEventManagementFragmentToEventChatFragment(currentEvent.eventId)
        findNavController().navigate(action)
    }

    fun alertCardReceived(){
        //#TODO Update In future

    }


}



