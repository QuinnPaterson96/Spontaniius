package spontaniius.ui.find_event

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import spontaniius.common.PlacesViewModel
import spontaniius.data.remote.models.PlaceSuggestion
import spontaniius.ui.MainActivity
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
    var currLatLng: LatLng? = null
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

        this.googleMap=googleMap
        googleMap.setOnMapLongClickListener { latLng ->

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment

        val view: View = inflater.inflate(R.layout.fragment_find_event, container, false)
        swipeContainer = view.findViewById(R.id.swipeContainer)
        recyclerView = view.findViewById(R.id.recyclerview)
        swipeContainer = view.findViewById(R.id.swipeContainer)
        listButton = view.findViewById(R.id.listButton)
        mapButton = view.findViewById(R.id.mapButton)
        hintButton = view.findViewById(R.id.get_events_button)
        hintCreateEventButton = view.findViewById(R.id.create_event_hint_button)
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

        hintCreateEventButton.setOnClickListener {
            findNavController().navigate(R.id.createEventFragment)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)

        recyclerView.setLayoutManager(LinearLayoutManager(view.context))
        recyclerView.adapter = viewAdapter


        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener() {
            if(currLatLng!=null)
                swipeContainer.isRefreshing = true
                try {
                    viewModel.fetchEvents(
                        lat = currLatLng!!.latitude,
                        lng = currLatLng!!.longitude,
                        gender = null
                    )
                }catch (error: Exception){
                    Log.e("Find Event Fragment", error.toString())
                }
        }
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )


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
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11.5f))
            }
        }

        viewModel.events.observe(viewLifecycleOwner) { events ->

            if (events.isEmpty()){
                hintButton.visibility = VISIBLE
                hintText.visibility = VISIBLE
                hintCreateEventButton.visibility = VISIBLE
            }else{
                hintText.visibility = GONE
                hintCreateEventButton.visibility = GONE
                hintButton.visibility = GONE

            }
            val eventTiles = events.map { EventTile.fromDomain(it, requireContext()) }
            processEventTiles(eventTiles)
        }

        viewModel.eventError.observe(viewLifecycleOwner){
            swipeContainer.isRefreshing = false
        }


        getCurrentLocation()
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



    private fun processEventTiles(eventTiles: List<EventTile>) {
        googleMap?.clear()  // Clear previous markers

        val boundsBuilder = LatLngBounds.builder()

        for (event in eventTiles) {
            val location = LatLng(event.latitude, event.longitude)
            googleMap?.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(event.title)
            )
            boundsBuilder.include(location)
        }

        if (currLatLng!=null){
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currLatLng!!, 11.5f))
        }


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
                                FindEventFragmentDirections.actionFindEventFragmentToEventJoinFragment(
                                    eventId = eventView.eventId.toInt(),
                                    isEventOwner = false
                                )
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        })

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
