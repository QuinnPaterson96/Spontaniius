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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONArray
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.domain.models.Event
import spontaniius.domain.models.User
import spontaniius.ui.event_join.EventJoinViewModel
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
    lateinit var detailsToggleButton: Button
    lateinit var joinFromDetailsButton:Button
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
    var viewingDetails = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        fragmentView = inflater.inflate(R.layout.fragment_event_management, container, false)

        val eventId: Int = arguments?.getInt("eventId") ?: 0  // Default value if null


        // Both joinees and mangers go through here to initialize some details
        detailsToggleButton = fragmentView.findViewById<View>(R.id.detailsButton) as Button
        detailsToggleButton.setOnClickListener{

        }


        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(callback)


        joinFromDetailsButton.setOnClickListener{
            viewModel.joinEvent(eventId)
            goToEventChat()
        }

        // makes it so you have your own card already collected.

        val ownCardJsonArray = JSONArray()

        viewModel.userDetails.observe(viewLifecycleOwner){ user ->
            if (user != null) {
                currentUser = user
                ownCardJsonArray.put(0, user.cardId)
            }
        }

        viewModel.eventJoined.observe(viewLifecycleOwner){joined ->
            if(joined){
                //# TODO make it so this updates button
            }
        }


        viewModel.eventDetails.observe(viewLifecycleOwner){ event ->
            if (event != null) {
                currentEvent = event
                populateDetails(fragmentView)

            }
        }
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


        val location: LatLng = LatLng(currentEvent.latitude, currentEvent.longitude)

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

    fun goToEventChat(){
        val action =
            eventId?.let {
                EventJoinFragmentDirections.actionEventJoinFragmentToEventChatFragment(
                    eventId = it,
                )
            }
        if (action != null) {
            findNavController().navigate(action)
        }
    }

    fun alertCardReceived(){
        //#TODO Update In future

    }


}



