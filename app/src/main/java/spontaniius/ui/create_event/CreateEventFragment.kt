package spontaniius.ui.create_event

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import dagger.hilt.android.AndroidEntryPoint
import com.spontaniius.R
import spontaniius.data.remote.models.CreateEventRequest
import java.util.*

val LOCATION_PERMISSION_REQUEST_CODE = 1
val INVITATION_RADIUS_OPTIONS = listOf(500, 2000, 5000) // Corresponding radius values


@AndroidEntryPoint
class CreateEventFragment : Fragment(), MapsFragment.MapsInteractionListener {

    private val viewModel: CreateEventViewModel by viewModels()

    private lateinit var eventLoad: ProgressBar
    private lateinit var titleField: EditText
    private lateinit var descriptionField: EditText
    private lateinit var genderSpinner: Spinner
    private lateinit var iconSelectButton: ImageButton
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var startTimePicker: TimePicker
    private lateinit var endTimePicker: TimePicker
    private lateinit var invitationTypePicker: RadioGroup
    private lateinit var selectedButton: RadioButton


    private var selectedGender: String = "Any"
    private var selectedIcon: String = ""
    private var userLatLng: LatLng? = null
    private var stockTitle = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val viewLayout = inflater.inflate(R.layout.fragment_create_event, container, false)

        // Initialize views
        titleField = viewLayout.findViewById(R.id.event_title)
        descriptionField = viewLayout.findViewById(R.id.event_description)
        genderSpinner = viewLayout.findViewById(R.id.gender_select_spinner)
        eventLoad = viewLayout.findViewById(R.id.loading)
        iconSelectButton = viewLayout.findViewById(R.id.event_icon)
        startTimePicker = viewLayout.findViewById<TimePicker>(R.id.event_start_time_picker)
        endTimePicker = viewLayout.findViewById<TimePicker>(R.id.event_end_time_picker)
        invitationTypePicker = viewLayout.findViewById<RadioGroup>(R.id.invite_group)


        // Set up Gender Spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.gender_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = adapter
        }



        // Set up Create Event Button
        viewLayout.findViewById<Button>(R.id.create_event_button).setOnClickListener {
            validateAndCreateEvent()
        }

        // Set up Locate Me Button
        viewLayout.findViewById<ImageView>(R.id.locate_me_button_create).setOnClickListener {
            viewModel.getCurrentLocation()
        }

        // Set up Icon Selection Button
        iconSelectButton.setOnClickListener {
            IconSelectPopup()
        }

        // Set up Google Places Autocomplete
        Places.createClient(requireContext())
        autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("CreateEventFragment", "Place selected: ${place.name}")
                place.latLng?.let {
                    userLatLng = it
                    viewModel.getLocationFromAddress("${it.latitude}, ${it.longitude}", apiKey = getString(R.string.google_api_key))
                }
            }

            override fun onError(status: com.google.android.gms.common.api.Status) {
                Log.e("CreateEventFragment", "Error selecting place: ${status.statusMessage}")
            }
        })

        // **🔹 Title-Based Icon Prediction**
        titleField.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                predictIconFromText(s.toString())
            }
        })

        viewModel.locationPermissionNeeded.observe(viewLifecycleOwner) { needed ->
            if (needed) {
                requestLocationPermissions()
            }
        }

        viewModel.location.observe(viewLifecycleOwner) { location ->
            userLatLng = location
        }


        return viewLayout
    }

    private fun validateAndCreateEvent() {
        val title = titleField.text.toString().trim()
        val description = descriptionField.text.toString().trim()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Title and description cannot be empty!", Toast.LENGTH_LONG).show()
            return
        }

        if (userLatLng == null) {
            Toast.makeText(requireContext(), "Please select a location.", Toast.LENGTH_LONG).show()
            return
        }


        val invitationSelectorPosition = invitationTypePicker.indexOfChild(selectedButton).coerceIn(0, INVITATION_RADIUS_OPTIONS.lastIndex)
        val invitationRadius = INVITATION_RADIUS_OPTIONS[invitationSelectorPosition]

        eventLoad.visibility = View.VISIBLE

        val startDateString = getFormattedDateString(startTimePicker.hour, startTimePicker.minute)
        val endDateString = getFormattedDateString(endTimePicker.hour, endTimePicker.minute)

        val request = CreateEventRequest(
            title = title,
            description = description,
            gender = selectedGender,
            icon = selectedIcon,
            event_starts = startDateString,
            event_ends = endDateString,
            latitude = userLatLng!!.latitude,
            longitude = userLatLng!!.longitude,
            max_radius = invitationRadius,
            card_id = 1 // TODO get card from user repository
        )

        viewModel.createEvent(request)
    }

    private fun getFormattedDateString(hour: Int, minute: Int): String {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        return String.format(Locale.US, "%04d-%02d-%02dT%02d:%02d:00Z", year, month, day, hour, minute)
    }



    private fun predictIconFromText(text: String) {
        val lowerText = text.lowercase()
        when {
            lowerText.contains("coffee") || lowerText.contains("tea") -> setIcon(R.drawable.activity_coffee, "Meet me for coffee")
            lowerText.contains("walk") -> setIcon(R.drawable.activity_walk, "Let's go for a walk")
            lowerText.contains("food") || lowerText.contains("dinner") || lowerText.contains("lunch") || lowerText.contains("breakfast") || lowerText.contains("eat") -> setIcon(R.drawable.activity_eating, "Let's grab some food")
            lowerText.contains("bike") -> setIcon(R.drawable.activity_bike, "Let's go for a bike ride")
            lowerText.contains("beer") || lowerText.contains("wine") -> setIcon(R.drawable.activity_drinks, "Let's go have some drinks")
        }
    }

    private fun setIcon(iconRes: Int, defaultTitle: String) {
        iconSelectButton.setImageResource(iconRes)
        selectedIcon = iconRes.toString()

        if (stockTitle || titleField.text.toString().isEmpty()) {
            titleField.setText(defaultTitle)
            stockTitle = true
        }
    }

    private fun IconSelectPopup() {
        val popup = PopupMenu(requireContext(), iconSelectButton)
        popup.menuInflater.inflate(R.menu.event_icon_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.drinks -> setIcon(R.drawable.activity_drinks, "Let's go have some drinks")
                R.id.bike -> setIcon(R.drawable.activity_bike, "Let's go for a bike ride")
                R.id.eating -> setIcon(R.drawable.activity_eating, "Let's grab some food")
                R.id.sports -> setIcon(R.drawable.activity_sports, "Let's go play")
                R.id.walk -> setIcon(R.drawable.activity_walk, "Let's go for a walk")
                R.id.videogame -> setIcon(R.drawable.activity_videogame, "Let's play some video games")
                R.id.coffee -> setIcon(R.drawable.activity_coffee, "Meet me for coffee")
                R.id.other_activity -> setIcon(R.drawable.activity_other, "Join me for something fun!")
            }
            true
        }
        popup.show()
    }

    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            viewModel.permissionGranted() // Reset permission flag in ViewModel
            viewModel.getCurrentLocation() // Retry fetching location
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onLocationSelected(latLng: LatLng) {
        this.userLatLng = latLng
    }
}
