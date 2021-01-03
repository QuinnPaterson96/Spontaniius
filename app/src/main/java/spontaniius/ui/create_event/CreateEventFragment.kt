package spontaniius.ui.create_event

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import spontaniius.R
import spontaniius.dependency_injection.VolleySingleton
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import org.json.JSONArray
import org.json.JSONObject
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
    private val radius1 = 100
    private val radius2 = 500
    private val radius3 = 1000
    private val locationAPIKey="AIzaSyDftsoTlkMRu33vd6FLeWh-rzc0p0Ttt6k"// Make this refeence google maps api key
    //    TODO: event icon with string implementation
    private var eventIcon: String = ""



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewLayout = inflater.inflate(R.layout.fragment_create_event, container, false)
        eventIconView = viewLayout.findViewById(R.id.event_icon)
        val iconSelectButton = viewLayout.findViewById<ImageButton>(R.id.event_icon)
        iconSelectButton.setOnClickListener {
//            TODO: Select some icons from a set of chosen ones here
//            TODO: Go through icons with quinn and select a few good ones to use
        }
        val locationButton = viewLayout.findViewById<Button>(R.id.location_button)
        locationButton.setOnClickListener {
            listenerCreateEvent?.selectLocation()
        }
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
                Log.i("AddLocationFragment.TAG", p0.statusMessage)
            }

        })

        return viewLayout
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
                var currLatLng:LatLng  = LatLng(locationJSON.get("lat").toString().toDouble(), locationJSON.get("lng").toString().toDouble())
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
//      pattern = "yy-MM-dd hh:mm:00"
        val dateStringBuilder = StringBuilder()
        dateStringBuilder.append(year)
        dateStringBuilder.append("-")
        dateStringBuilder.append(month)
        dateStringBuilder.append("-")
        dateStringBuilder.append(day)
        dateStringBuilder.append(" ")
        dateStringBuilder.append(hour)
        dateStringBuilder.append(":")
        dateStringBuilder.append(minute)
        dateStringBuilder.append(":")
        dateStringBuilder.append("00")
        // Possible future timezone insert
        dateStringBuilder.append(TimeZone.getDefault().getDisplayName(TimeZone.getDefault().inDaylightTime(Calendar.getInstance().time), TimeZone.SHORT))
        return dateStringBuilder.toString()
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
