package spontaniius.ui.find_event

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
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
import spontaniius.R
import spontaniius.dependency_injection.VolleySingleton
import spontaniius.ui.BottomNavigationActivity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class FindEventFragment : Fragment() {



    private var eventList: ArrayList<EventTile> = ArrayList()
    private lateinit var recyclerView: RecyclerView
    private var viewAdapter: RecyclerView.Adapter<*> = EventFindAdapter(eventList)
    private lateinit var viewManager: RecyclerView.LayoutManager
    lateinit var swipeContainer:SwipeRefreshLayout
    lateinit var streetName:String
    lateinit var currLatLng:String
    lateinit var mapFragment: SupportMapFragment
    private var googleMap: GoogleMap? = null
    lateinit var mapButton: Button
    lateinit var listButton: Button
    var ViewList=1 // either 1 for list mode or 0 for map mode


    var locationAPIKey="AIzaSyDftsoTlkMRu33vd6FLeWh-rzc0p0Ttt6k"// Make this refeence google maps api key

    companion object {
        fun newInstance() = FindEventFragment()
    }

    private lateinit var viewModel: FindEventViewModel
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

        listButton.setOnClickListener{
            switchToList()
        }

        mapButton.setOnClickListener{
            switchToMap()
        }



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)

        recyclerView.setLayoutManager(LinearLayoutManager(view.context))
        recyclerView.setAdapter(viewAdapter)

        /*
        streetName="909 Thistle Place, Britannia Beach, BC"
        getLocationFromAddress(streetName)
        */
        val locateMeButton = view.findViewById<ImageView>(R.id.locate_me_button);
        locateMeButton.setOnClickListener {
            getCurrentLocation()
            }

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener() {
            getEvents(currLatLng)

        }
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )

        // Initialize Places.

        // Initialize Places.
        Places.initialize(view.context, locationAPIKey)

// Create a new Places client instance.

// Create a new Places client instance.
        val placesClient = Places.createClient(view.context)
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

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment?.getMapAsync(callback)
        mapFragment.view?.visibility = View.GONE
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FindEventViewModel::class.java)
        // TODO: Use the ViewModel
    }


    fun getCurrentLocation(){
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
                (activity as BottomNavigationActivity),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
            ActivityCompat.requestPermissions(
                (activity as BottomNavigationActivity),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 2
            )

            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currLatLng = (location.latitude.toString()+","+location.longitude.toString())
                }
                getEvents(currLatLng)
            }

        }

    // Not needed as google autocomplete returns coordinate, will keep if we ever want to move away from using google's api + fragment
    fun getLocationFromAddress(strAddress: String?){

        val url = "https://maps.googleapis.com/maps/api/geocode/json?address="+strAddress+"&key="+locationAPIKey
        val getLocationRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                val JSONResponse = JSONObject(response.toString())
                val resultJSONArray: JSONArray = JSONResponse.get("results") as JSONArray
                val resultJSON: JSONObject = resultJSONArray.getJSONObject(0)
                val geometryJSON: JSONObject = resultJSON.get("geometry") as JSONObject
                val locationJSON: JSONObject = geometryJSON.get("location") as JSONObject
                currLatLng =
                    locationJSON.get("lat").toString() + ", " + locationJSON.get("lng").toString()

                getEvents(currLatLng)


            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }

        )
        val queue = this.context?.let { VolleySingleton.getInstance(it).requestQueue }
        queue?.add(getLocationRequest)
    }

    /*
    fun getIconFromString(iconName:String){
        if(iconName=="bike"){}


    }
*/

    fun getEvents(userAddress: String){
        swipeContainer.isRefreshing = true
        val queue = this.context?.let { VolleySingleton.getInstance(it).requestQueue }

        // ...

        // Add a request (in this example, called stringRequest) to your RequestQueue.

             var streetAddress =userAddress // "49.627795, -123.199644"
        if (this.getContext()?.let { ContextCompat.checkSelfPermission(
                it,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) } != PackageManager.PERMISSION_GRANTED )

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                streetAddress=location.toString()
            }

        val currtime = Calendar.getInstance().time
        val url =
            "https://2xhan6hu38.execute-api.us-west-2.amazonaws.com/default/GetEventsInArea?streetAddress=$streetAddress&currentTime=$currtime";
            val getEventsRequest = StringRequest(Request.Method.GET, url,
                { response ->
                    eventList.clear()
                    val newList: ArrayList<EventTile> = ArrayList()
                    val eventJSONArray = JSONArray(response.toString())

                    for (i in 0 until eventJSONArray.length()) {

                        val event: JSONObject = eventJSONArray.getJSONObject(i)
                        val endTimeString = event.get("eventends")
                        val endTime = ZonedDateTime.parse(
                            endTimeString as CharSequence?, DateTimeFormatter.ofPattern(
                                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
                            )
                        )

                        val currentTime = currtime.time / 1000
                        val eventTime = endTime.toEpochSecond()
                        val milliseconds = eventTime - currentTime
                        val minutesFromEnd = milliseconds.toInt() / 60

                        try {
                            newList.add(
                                EventTile(
                                    event.get("icon").toString().toInt(),
                                    event.get("eventtitle").toString(),
                                    event.get("eventtext").toString(),
                                    event.get("distance").toString(),
                                    "ends in " + minutesFromEnd.toString() + " mins",
                                    event.get("streetaddress").toString(),
                                    this.context
                                )
                            )
                        } finally {

                        }

                        /*
                        * "cardids": null,
                          "eventid": 1,
                          "eventtitle": "Lamb party",
                          "eventtext": "Test from Lambda",
                          "genderrestrict": "any",
                          "streetaddress": {
                            "x": 48.4335854,
                            "y": -123.3371036
                          },
                          "icon": "01.jpg",
                          "maxradius": 100,
                          "eventstarts": "11:39:27.331481",
                          "eventends": "11:39:27.331481"*/
                    }

                    eventList.addAll(newList)
                    viewAdapter.notifyDataSetChanged()

                    for (i in 0 until eventList.size) {
                        var event = eventList.get(i)
                        val latlong = JSONObject(event.location)
                        val location = LatLng(latlong.getDouble("x"), latlong.getDouble("y"))
                        googleMap?.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title(event.title)
                               // .icon(BitmapDescriptorFactory.fromResource(R.drawable.wave)
                                )
                    }



/*
                    mapFragment?.apply {
                        val sydney = LatLng(-33.852, 151.211)
                        mapFragment.addMarker(
                            MarkerOptions()
                                .position(sydney)
                                .title("Marker in Sydney")
                        )
                    }

 */
                    if (mapFragment != null) {
                        val latlong = streetAddress.split(",".toRegex()).toTypedArray()
                        val latitude = latlong[0].toDouble()
                        val longitude = latlong[1].toDouble()
                        val location = LatLng(latitude, longitude)
                        val zoomLevel = 6.0f

                        if (googleMap != null) {
                            googleMap!!.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            googleMap!!.getUiSettings().setMyLocationButtonEnabled(false);
                            googleMap!!.setMyLocationEnabled(false);
                            googleMap!!.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    location,
                                    zoomLevel
                                )
                            )
                        }
                    }
                    swipeContainer.isRefreshing = false


                },
                { error ->
                    error.printStackTrace()
                    swipeContainer.isRefreshing = false
                }
            )
        queue?.add(getEventsRequest)
        }

    fun switchToMap(){
        mapFragment.view?.visibility = View.VISIBLE
        swipeContainer.visibility=View.GONE
        listButton.setBackgroundColor(Color.GRAY)
        mapButton.setBackgroundColor(Color.YELLOW)
    }

    fun switchToList(){
        mapFragment.view?.visibility = View.GONE
        swipeContainer.visibility=View.VISIBLE
        listButton.setBackgroundColor(Color.YELLOW)
        mapButton.setBackgroundColor(Color.GRAY)

    }

    /*
     override fun onMapReady(googleMap: GoogleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        val sydney = LatLng(-33.852, 151.211)
        googleMap.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

     */
    }
