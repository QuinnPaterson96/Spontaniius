package com.example.spontaniius.ui.find_event

import android.content.pm.PackageManager
import android.icu.util.UniversalTimeScale.toLong
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.spontaniius.R
import com.example.spontaniius.dependency_injection.VolleySingleton
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Math.round
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
    var locationAPIKey="AIzaSyDftsoTlkMRu33vd6FLeWh-rzc0p0Ttt6k"// Make this refeence google maps api key

    companion object {
        fun newInstance() = FindEventFragment()
    }

    private lateinit var viewModel: FindEventViewModel
    lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_find_event, container, false)
        swipeContainer = view.findViewById(R.id.swipeContainer)
        recyclerView = view.findViewById(R.id.recyclerview)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.context)

        recyclerView.setLayoutManager(LinearLayoutManager(view.context))
        recyclerView.setAdapter(viewAdapter)

        /*
        streetName="909 Thistle Place, Britannia Beach, BC"
        getLocationFromAddress(streetName)
        */


        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener() {
            getEvents(currLatLng)

        }
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light)

        // Initialize Places.

        // Initialize Places.
        Places.initialize(view.context, locationAPIKey)

// Create a new Places client instance.

// Create a new Places client instance.
        val placesClient = Places.createClient(view.context)
        val autocompleteFragment: AutocompleteSupportFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))



        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                Log.i("AddLocationFragment.TAG", "Place: " + place.name)
                val coords: LatLng? = place.latLng
                if (coords != null) {
                    getLocationFromAddress(coords.latitude.toString()+", "+coords.longitude.toString())
                }
            }

            override fun onError(p0: Status) {
                Log.i("AddLocationFragment.TAG", p0.statusMessage)
            }

        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FindEventViewModel::class.java)
        // TODO: Use the ViewModel
    }


    // Not needed as google autocomplete returns coordinate, will keep if we ever want to move away from using google's api + fragment
    fun getLocationFromAddress(strAddress: String?){

        val url = "https://maps.googleapis.com/maps/api/geocode/json?address="+strAddress+"&key="+locationAPIKey
        val getLocationRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                val JSONResponse= JSONObject(response.toString())
                val resultJSONArray:JSONArray = JSONResponse.get("results") as JSONArray
                val resultJSON:JSONObject = resultJSONArray.getJSONObject(0)
                val geometryJSON:JSONObject = resultJSON.get("geometry") as JSONObject
                val locationJSON:JSONObject = geometryJSON.get("location") as JSONObject
                currLatLng = locationJSON.get("lat").toString() + ", "+locationJSON.get("lng").toString()

                getEvents(currLatLng)


            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }

        )
        val queue = this.context?.let { VolleySingleton.getInstance(it).requestQueue }
        queue?.add(getLocationRequest)
    }


    fun getEvents(userAddress:String){
        swipeContainer.isRefreshing = true
        val queue = this.context?.let { VolleySingleton.getInstance(it).requestQueue }

        // ...

        // Add a request (in this example, called stringRequest) to your RequestQueue.

             var streetAddress =userAddress // "49.627795, -123.199644"
        if (this.getContext()?.let { ContextCompat.checkSelfPermission(it,android.Manifest.permission.ACCESS_COARSE_LOCATION ) } != PackageManager.PERMISSION_GRANTED )

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                streetAddress=location.toString()
            }


        val url =
            "https://2xhan6hu38.execute-api.us-west-2.amazonaws.com/default/GetEventsInArea?streetAddress=$streetAddress"
            val getEventsRequest = StringRequest(Request.Method.GET, url,
                { response ->
                    eventList.clear()
                    val newList: ArrayList<EventTile> = ArrayList()
                val eventJSONArray= JSONArray(response.toString())
                    for (i in 0 until eventJSONArray.length()) {
                        val currtime = Calendar.getInstance().time
                        //currtime.
                        val event:JSONObject = eventJSONArray.getJSONObject(i)
                        val endtime = event.get("eventends")


                        newList.add(EventTile(R.drawable.ic_cofee_24, event.get("eventtitle").toString(),
                            event.get("eventtext").toString(),
                            event.get("distance").toString(),
                            event.get("eventends").toString(),
                            event.get("streetaddress").toString(),
                            this.context
                        ))

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
                    swipeContainer.isRefreshing = false

            },
                { error ->
                    error.printStackTrace()
                    swipeContainer.isRefreshing = false
                }
        )
        queue?.add(getEventsRequest)
        }
    }
