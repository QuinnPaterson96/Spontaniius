package spontaniius.ui.create_event

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
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
import spontaniius.R
import spontaniius.dependency_injection.VolleySingleton
import spontaniius.ui.BottomNavigationActivity
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CreateEventFragment.OnCreateEventFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CreateEventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateEventFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var listenerCreateEvent: OnCreateEventFragmentInteractionListener? = null
    private lateinit var eventIconView: ImageButton
    private lateinit var gender: String
     private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var iconSelectButton: ImageButton
    lateinit var eventIconValue: String
    private val radius1 = 100
    private val radius2 = 500
    private val radius3 = 1000
    private val locationAPIKey="AIzaSyDftsoTlkMRu33vd6FLeWh-rzc0p0Ttt6k"// Make this refeence google maps api key
    //    TODO: event icon with string implementation
    private var eventIcon: String = ""
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var titleField: EditText
    lateinit var descriptionField:EditText
    // tracks if using stock title
    var stockTitle = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewLayout = inflater.inflate(R.layout.fragment_create_event, container, false)


        val locateMeButton = viewLayout.findViewById<ImageView>(R.id.locate_me_button_create);
        locateMeButton.setOnClickListener {
            getCurrentLocation()
        }
        titleField = viewLayout.findViewById(R.id.event_title)
        descriptionField = viewLayout.findViewById(R.id.event_description)

        eventIconView = viewLayout.findViewById(R.id.event_icon)
        iconSelectButton = viewLayout.findViewById<ImageButton>(R.id.event_icon)
        iconSelectButton.setOnClickListener {
//            TODO: Select some icons from a set of chosen ones here
//            TODO: Go through icons with quinn and select a few good ones to use
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                IconSelectPopup()
            }
        }

        /*
        val locationButton = viewLayout.findViewById<Button>(R.id.location_button)
        locationButton.setOnClickListener {
            listenerCreateEvent?.selectLocation()
        }
         */


        val genderSpinner = viewLayout.findViewById<Spinner>(R.id.gender_select_spinner)
        ArrayAdapter.createFromResource(
            listenerCreateEvent as Context,
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = arrayAdapter
        }
        genderSpinner.onItemSelectedListener = this
        val createEventButton = viewLayout.findViewById<Button>(R.id.create_event_button)
        createEventButton.setOnClickListener {
            val title = viewLayout.findViewById<EditText>(R.id.event_title)
            val description = viewLayout.findViewById<EditText>(R.id.event_description)
            val startTime = viewLayout.findViewById<TimePicker>(R.id.event_start_time_picker)
            val endTime = viewLayout.findViewById<TimePicker>(R.id.event_end_time_picker)
            val invitationType = viewLayout.findViewById<RadioGroup>(R.id.invite_group)

            val debugIcon = eventIcon;

            when {
                title.text.toString() == "" -> {
                    Toast.makeText(
                        context,
                        getString(R.string.warning_input_title),
                        Toast.LENGTH_LONG
                    ).show()
                }
                description.text.toString() == "" -> {
                    Toast.makeText(context, R.string.warning_input_description, Toast.LENGTH_LONG)
                        .show()
                }
                else -> {
                    val currentDate = Calendar.getInstance(TimeZone.getDefault())
                    val year = currentDate.get(Calendar.YEAR)
                    val month = currentDate.get(Calendar.MONTH) + 1
                    val day = currentDate.get(Calendar.DAY_OF_MONTH)
                    val selectedInvitationID = invitationType.checkedRadioButtonId
                    val selectedButton = viewLayout.findViewById<RadioButton>(selectedInvitationID)
                    val invitationPosition = invitationType.indexOfChild(selectedButton)
                    val invitation = when {
                        invitationPosition < 1 -> radius1
                        invitationPosition == 1 -> radius2
                        else -> radius3
                    }
                    listenerCreateEvent?.createEvent(
                        title.text.toString(),
                        description.text.toString(),
                        eventIcon,
                        getDateString(year, month, day, startTime.hour, startTime.minute),
                        getDateString(year, month, day, endTime.hour, endTime.minute),
                        gender,
                        invitation
                    )
                }
            }
        }


        val placesClient = view?.context?.let { Places.createClient(it) }
        autocompleteFragment= childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

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

        titleField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var currentTitle = s.toString()
                if (currentTitle.contains("coffee") || currentTitle.contains("tea")) {
                    iconSelectButton.setImageResource(R.drawable.activity_coffee)
                    eventIcon = R.drawable.activity_coffee.toString()
                }
                if (currentTitle.contains("walk")) {
                    iconSelectButton.setImageResource(R.drawable.activity_walk)
                    eventIcon = R.drawable.activity_walk.toString()
                }
                if (currentTitle.contains("food") || currentTitle.contains("dinner") || currentTitle.contains(
                        "lunch"
                    ) || currentTitle.contains("breakfast")
                ) {
                    iconSelectButton.setImageResource(R.drawable.activity_eating)
                    eventIcon = R.drawable.activity_eating.toString()
                }
                if (currentTitle.contains("bike")) {
                    iconSelectButton.setImageResource(R.drawable.activity_bike)
                    eventIcon = R.drawable.activity_bike.toString()
                }
                if (currentTitle.contains("beer") || currentTitle.contains("wine")) {
                    iconSelectButton.setImageResource(R.drawable.activity_drinks)
                    eventIcon = R.drawable.activity_drinks.toString()
                }
                stockTitle = false
            }
        })


        return viewLayout
    }

    private fun getCurrentLocation(){
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireView().context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if(location!=null){
                var currLatLng:LatLng  = LatLng(
                    location?.latitude.toString().toDouble(),
                    location?.longitude.toString().toDouble()
                )

                listenerCreateEvent?.googleLocationSelect(currLatLng)
                val etPlace = autocompleteFragment.view?.findViewById(R.id.places_autocomplete_search_input) as EditText
                etPlace.hint = currLatLng.toString()
            }
            }

    }


    fun getLocationFromAddress(strAddress: String?){

        val url = "https://maps.googleapis.com/maps/api/geocode/json?address="+strAddress+"&key="+locationAPIKey
        val getLocationRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val JSONResponse = JSONObject(response.toString())
                val resultJSONArray: JSONArray = JSONResponse.get("results") as JSONArray
                val resultJSON: JSONObject = resultJSONArray.getJSONObject(0)
                val geometryJSON: JSONObject = resultJSON.get("geometry") as JSONObject
                val locationJSON: JSONObject = geometryJSON.get("location") as JSONObject
                var currLatLng: LatLng = LatLng(
                    locationJSON.get("lat").toString().toDouble(), locationJSON.get(
                        "lng"
                    ).toString().toDouble()
                )
                locationJSON.get("lat").toString() + ", " + locationJSON.get("lng").toString()

                listenerCreateEvent?.googleLocationSelect(currLatLng)


            },
            { error ->
                error.printStackTrace()
            }

        )
        val queue = this.context?.let { VolleySingleton.getInstance(it).requestQueue }
        queue?.add(getLocationRequest)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
//        Do nothing
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCreateEventFragmentInteractionListener) {
            listenerCreateEvent = context
            if (!::gender.isInitialized) {
                gender = context.resources.getStringArray(R.array.gender_array)[0]
            }
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerCreateEvent = null
    }


    private fun getDateString(year: Int, month: Int, day: Int, hour: Int, minute: Int): String {
        // If we ever need to format for years, this app will have been successful beyond my wildest imagination

        val dateStringBuilder = StringBuilder()
        dateStringBuilder.append(year)
        dateStringBuilder.append("-")
        dateStringBuilder.append(month)
        dateStringBuilder.append("-")
        dateStringBuilder.append((day))
        dateStringBuilder.append("T")
        dateStringBuilder.append(hour)
        dateStringBuilder.append(":")
        dateStringBuilder.append(minute)
        dateStringBuilder.append(":")
        dateStringBuilder.append("00")
        // Possible future timezone insert
        dateStringBuilder.append(
            TimeZone.getDefault().getDisplayName(
                TimeZone.getDefault().inDaylightTime(
                    Calendar.getInstance().time
                ), TimeZone.SHORT
            )
        )
        return dateStringBuilder.toString()




}

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun IconSelectPopup(){
        val popup = PopupMenu(context, iconSelectButton)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.event_icon_menu, popup.menu)
        popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
            override fun onMenuItemClick(item: MenuItem): Boolean {
                val titleTextEmpty = titleField.text.toString() == ""
                val emptyOrStock =
                    titleTextEmpty || stockTitle // If title is empty, or if its a stock title then a new stock title will be populated
                return when (item.getItemId()) {
                    R.id.drinks -> {
                        iconSelectButton.setImageResource(R.drawable.activity_drinks)
                        eventIcon = R.drawable.activity_drinks.toString()

                        if (emptyOrStock) {
                            titleField.setText("Let's go have some drinks")
                            stockTitle = true
                        }


                        return true
                    }
                    R.id.bike -> {
                        iconSelectButton.setImageResource(R.drawable.activity_bike)
                        eventIcon = R.drawable.activity_bike.toString()

                        if (emptyOrStock) {
                            titleField.setText("Let's go for a bike ride")
                            stockTitle = true

                        }

                        return true
                    }
                    R.id.eating -> {
                        iconSelectButton.setImageResource(R.drawable.activity_eating)
                        eventIcon = R.drawable.activity_eating.toString()
                        if (emptyOrStock) {
                            titleField.setText("Let's grab some food together")
                            stockTitle = true

                        }


                        return true

                    }
                    R.id.sports -> {
                        iconSelectButton.setImageResource(R.drawable.activity_sports)
                        eventIcon = R.drawable.activity_sports.toString()

                        if (emptyOrStock) {
                            titleField.setText("Let's go play")
                            stockTitle = true

                        }

                        return true

                    }
                    R.id.walk -> {
                        iconSelectButton.setImageResource(R.drawable.activity_walk)
                        eventIcon = R.drawable.activity_walk.toString()
                        if (emptyOrStock) {
                            titleField.setText("Let's go for a walk")
                            stockTitle = true

                        }


                        return true
                    }
                    R.id.videogame -> {
                        iconSelectButton.setImageResource(R.drawable.activity_videogame)
                        eventIcon = R.drawable.activity_videogame.toString()
                        if (emptyOrStock) {
                            titleField.setText("Let's play some ")
                            stockTitle = true

                        }

                        return true

                    }
                    R.id.coffee -> {
                        iconSelectButton.setImageResource(R.drawable.activity_coffee)
                        eventIcon = R.drawable.activity_coffee.toString()
                        if (emptyOrStock) {
                            titleField.setText("Meet me for coffee")
                            stockTitle = true

                        }

                        return true

                    }


                    else -> false
                }
            }
        })
        popup.setForceShowIcon(true)
        popup.show()
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnCreateEventFragmentInteractionListener {
        fun selectLocation(): Any?
        fun googleLocationSelect(latLng: LatLng):Any?
        fun createEvent(
            title: String,
            description: String,
            icon: String,
            startTime: String, //using unix timestamp
            endTime: String,
            gender: String,
            invitation: Int
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment CreateEventFragment.
         */
        @JvmStatic
        fun newInstance() =
            CreateEventFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}


