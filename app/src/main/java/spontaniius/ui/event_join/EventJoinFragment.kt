package spontaniius.ui.event_join

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
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
import com.google.firebase.database.DatabaseReference
import org.json.JSONArray
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.domain.models.Event
import spontaniius.domain.models.User
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*




/**
 * A simple [Fragment] subclass.
 * Use the [EventManagementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class EventJoinFragment : Fragment() {
    private val viewModel: EventJoinViewModel by viewModels()
    lateinit var messagesRef: DatabaseReference
    var currentUser: User? = null


    lateinit var input: EditText
    lateinit var fragmentView: View
    lateinit var exitButton: Button
    lateinit var chatButton: Button
    lateinit var joinButton: Button
    lateinit var getDirectionsButton: Button

    var manager = false
    var eventId: Int? = null
    private var googleMap: GoogleMap? = null
    lateinit var mapFragment: SupportMapFragment
    lateinit var currentEvent: Event
    private val callback = OnMapReadyCallback { googleMap ->

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
        fragmentView = inflater.inflate(R.layout.fragment_event_join, container, false)

        val eventId: Int = arguments?.getInt("eventId") ?: 0  // Default value if null


        // Both joinees and mangers go through here to initialize some details
        joinButton = fragmentView.findViewById<View>(R.id.joinButton) as Button
        joinButton.setOnClickListener{
            if(currentUser!=null){
                viewModel.joinEvent(userId = currentUser!!.id, cardId = currentUser?.cardId!!, eventId = currentEvent.eventId)
            }
            goToEventChat()
        }

        chatButton = fragmentView.findViewById<View>(R.id.chatButton) as Button
        chatButton.setOnClickListener{
            goToEventChat()
        }

        exitButton = fragmentView.findViewById<View>(R.id.exitButton) as Button
        exitButton.setOnClickListener{
            findNavController().navigate(R.id.findEventFragment)
        }


        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(callback)


        // makes it so you have your own card already collected.

        val ownCardJsonArray = JSONArray()

        viewModel.userDetails.observe(viewLifecycleOwner){ user ->
            if (user != null) {
                currentUser = user
                ownCardJsonArray.put(0, user.cardId)
            }
        }


        viewModel.eventDetails.observe(viewLifecycleOwner){ event ->
            // Handles that if user navigates back to here after joining event
            if (event != null) {
                currentEvent = event
                if (currentUser?.cardId in currentEvent.cardIds){
                    joinButton.visibility = GONE
                    chatButton.visibility = VISIBLE
                }
                populateDetails(fragmentView)
            }

        }
        viewModel.getUserDetails()
        viewModel.loadEventDetails(eventId)

        return fragmentView
    }




    // Basically fills the details screen
    private fun populateDetails(fragmentView: View) {
        val eventTitle = fragmentView.findViewById<View>(R.id.event_title) as TextView
        val eventDescription = fragmentView.findViewById<View>(R.id.event_description) as TextView
        val eventStarts = fragmentView.findViewById<View>(R.id.event_started) as TextView
        val eventEnds = fragmentView.findViewById<View>(R.id.event_ends) as TextView

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

        val dst = Calendar.getInstance().timeZone.dstSavings // Checks to see if daylight savings

        // Because postgres doesn't store timestamp we need to offset to local time. Currently manager keeps data from event creation, whereas fields are populated by api
        // For event joinees

        if(!manager){
            startTime = startTime.plusHours((Calendar.getInstance().timeZone.rawOffset.toLong() + dst) / 3600000)
            endTime = endTime.plusHours((Calendar.getInstance().timeZone.rawOffset.toLong() + dst) / 3600000)
        }



        val localEndTime = endTime.toLocalTime()
        val localStartTime = startTime.toLocalTime()


        eventStarts.text = LocalTime.parse(localStartTime.toString(),
            DateTimeFormatter.ofPattern("HH:mm")).format(
            DateTimeFormatter.ofPattern("hh:mm a"))
        eventEnds.text =
            LocalTime.parse(localEndTime.toString(), DateTimeFormatter.ofPattern("HH:mm")).format(
                DateTimeFormatter.ofPattern("hh:mm a"))


        // There's slight differences between how street address is stored in local object vs when retrieved from database, this handles that differece.


        googleMap?.addMarker(
            MarkerOptions()
                .position(LatLng(currentEvent.latitude, currentEvent.longitude))
                .title(currentEvent.title)
        )

        if (::mapFragment.isInitialized) {

            val zoomLevel = 15.0f

            if (googleMap != null) {
                googleMap!!.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap!!.getUiSettings().setMyLocationButtonEnabled(false);
                googleMap!!.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(currentEvent.latitude, currentEvent.longitude),
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

    fun goToEventChat(){
        val action =
            currentEvent.eventId.let {
                EventJoinFragmentDirections.actionEventJoinFragmentToEventChatFragment(
                    eventId = currentEvent.eventId,
                )
            }
        findNavController().navigate(action)
    }

    fun alertCardReceived(){
        //#TODO Update In future

    }


}



