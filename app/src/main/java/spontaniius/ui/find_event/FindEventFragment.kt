package spontaniius.ui.find_event

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import org.json.JSONArray
import org.json.JSONObject
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.ui.MainActivity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class FindEventFragment : Fragment() {




    private var eventList: ArrayList<EventTile> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private var viewAdapter: RecyclerView.Adapter<*> = EventFindAdapter(eventList)
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var swipeContainer:SwipeRefreshLayout
    lateinit var streetName:String
    lateinit var currLatLng: LatLng
    lateinit var mapFragment: SupportMapFragment
    private var googleMap: GoogleMap? = null
    lateinit var mapButton: Button
    lateinit var listButton: Button
    lateinit var hintButton: Button
    lateinit var hintCreateEventButton: Button
    lateinit var hintText: TextView
    var ViewList=1 // either 1 for list mode or 0 for map mode



    companion object {
        fun newInstance() = FindEventFragment()
    }

    private val viewModel: FindEventViewModel by viewModels()
    lateinit var fusedLocationClient: FusedLocationProviderClient
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



        this.googleMap=googleMap
        googleMap.setOnMapLongClickListener { latLng ->

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_find_event, container, false)
        swipeContainer = view.findViewById(R.id.swipeContainer)
        recyclerView = view.findViewById(R.id.recyclerview)
        swipeContainer = view.findViewById(R.id.swipeContainer)
        listButton = view.findViewById(R.id.listButton)
        mapButton = view.findViewById(R.id.mapButton)
        hintButton = view.findViewById(R.id.hint_get_events_button)
        hintText = view.findViewById(R.id.hint_text)

        listButton.setOnClickListener{
            switchToList()
        }

        mapButton.setOnClickListener{
            switchToMap()
        }


        hintButton.setOnClickListener {
            getCurrentLocation()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)

        recyclerView.setLayoutManager(LinearLayoutManager(view.context))
        recyclerView.adapter = viewAdapter


        val locateMeButton = view.findViewById<ImageView>(R.id.locate_me_button);
        locateMeButton.setOnClickListener {
            getCurrentLocation()
        }

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener() {
            if(::currLatLng.isInitialized)
                swipeContainer.isRefreshing = true
                viewModel.fetchEvents(lat = currLatLng.latitude, lng = currLatLng.longitude, gender = null) // Todo maybe fix gender stuff
        }
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        // Initialize Places.


        // Create a new Places client instance.
        val autocompleteFragment: AutocompleteSupportFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(
            Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG
            )
        )



        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                Log.i("AddLocationFragment.TAG", "Place: " + place.name)
                val coords: LatLng? = place.latLng
                if (coords != null) {
                    getLocationFromAddress(coords.latitude.toString() + ", " + coords.longitude.toString())
                }
            }

            override fun onError(p0: Status) {
                p0.statusMessage?.let { Log.i("AddLocationFragment.TAG", it) }
            }

        })

        viewModel.locationPermissionNeeded.observe(viewLifecycleOwner) { needed ->
            if (needed) {
                requestLocationPermissions()
            }
        }

        viewModel.currentLocation.observe(viewLifecycleOwner){ latLng->
            if (latLng != null) {
                swipeContainer.isRefreshing = true
                viewModel.fetchEvents(lat = latLng.latitude, lng = latLng.longitude, gender = null) // Todo maybe fix gender stuff
                currLatLng = latLng
            }
        }

        viewModel.events.observe(viewLifecycleOwner) { events ->
            val eventTiles = events.map { EventTile.fromDomain(it) }
            processEventTiles(eventTiles)
        }



        return view
    }

    private fun requestLocationPermissions() {
        if (this.context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && this.context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                (activity as MainActivity),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
            ActivityCompat.requestPermissions(
                (activity as MainActivity),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 2
            )

            return
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment?.getMapAsync(callback)
        mapFragment.view?.visibility = GONE
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }


    fun getCurrentLocation(){
        viewModel.getCurrentLocation()
    }

    // Not needed as google autocomplete returns coordinate, will keep if we ever want to move away from using google's api + fragment
    fun getLocationFromAddress(strAddress: String){
        viewModel.getLocationFromAddress(strAddress, getString(R.string.google_api_key))
    }



    private fun processEventTiles(eventTiles: List<EventTile>) {
        val oldSize = eventList.size  // Store previous size before clearing

        eventList.clear()

        if (oldSize > 0) {
            viewAdapter.notifyItemRangeRemoved(0, oldSize)
        }

        eventList.addAll(eventTiles)

        if (eventTiles.isNotEmpty()) {
            viewAdapter.notifyItemRangeInserted(0, eventTiles.size)
        }


        recyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View, left: Int, top: Int, right: Int, bottom: Int,
                oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
            ) {
                for (i in 0 until recyclerView.childCount) {
                    val holder = recyclerView.findViewHolderForAdapterPosition(i)
                    if (holder != null) {
                        val eventView = holder as EventFindAdapter.EventCardViewHolder
                        eventView.details.setOnClickListener {
                            val action =
                                FindEventFragmentDirections.actionFindEventFragmentToEventManagementFragment(
                                    eventId = eventView.eventId,
                                    isEventOwner = false
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        })

        for (event in eventTiles) {
            val location = LatLng(event.latitude, event.longitude)
            googleMap?.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(event.title)
            )
        }

        if (eventTiles.isNotEmpty()) {
            val firstEvent = eventTiles.first()
            val location = LatLng(firstEvent.latitude, firstEvent.longitude)
            val zoomLevel = 10.0f

            googleMap?.apply {
                setMapType(GoogleMap.MAP_TYPE_NORMAL)
                moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
            }
        }
        swipeContainer.isRefreshing = false
    }


    fun switchToMap(){
        mapFragment.view?.visibility = VISIBLE
        swipeContainer.visibility= GONE
        listButton.setBackgroundColor(getResources().getColor(R.color.colorNeutral))
        mapButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
    }

    fun switchToList(){
        mapFragment.view?.visibility = GONE
        swipeContainer.visibility= VISIBLE
        listButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary))
        mapButton.setBackgroundColor(getResources().getColor(R.color.colorNeutral))


    }
}
