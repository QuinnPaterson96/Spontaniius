package spontaniius.ui.create_event

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import dagger.hilt.android.AndroidEntryPoint
import com.spontaniius.R
import spontaniius.ui.find_event.FindEventFragmentDirections
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
            override fun afterTextChanged(s: android.text.Editable) {
                stockTitle=false
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })

        viewModel.locationPermissionNeeded.observe(viewLifecycleOwner) { needed ->
            if (needed) {
                requestLocationPermissions()
            }
        }

        viewModel.location.observe(viewLifecycleOwner) { location ->
            userLatLng = location
            viewModel.getAddressFromLocation(location, getString(R.string.google_api_key))
        }

        viewModel.address.observe(viewLifecycleOwner){ address ->
            if (address == null){
                autocompleteFragment.setText("${userLatLng?.latitude},${userLatLng?.longitude}")
            }else{
                autocompleteFragment.setText(address)
            }
        }

        viewModel.eventCreated.observe(viewLifecycleOwner){ event ->
            eventLoad.visibility = View.GONE
            if(event !=null){
                val action =
                    CreateEventFragmentDirections.actionCreateEventFragmentToEventManagementFragment(
                        eventId = event.eventId,
                        isEventOwner = true
                    )
                findNavController().navigate(action)
            }

        }

        return viewLayout
    }

    private fun validateAndCreateEvent() {
        val title = titleField.text.toString().trim()
        val description = descriptionField.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Title cannot be empty!", Toast.LENGTH_LONG).show()
            return
        }

        if (userLatLng == null) {
            Toast.makeText(requireContext(), "Please select a location.", Toast.LENGTH_LONG).show()
            return
        }

        val selectedId = invitationTypePicker.checkedRadioButtonId
        val selectedRadioButton: RadioButton = (invitationTypePicker.findViewById<RadioButton>(selectedId))
        val invitationSelectorPosition = invitationTypePicker.indexOfChild(selectedRadioButton).coerceIn(0, INVITATION_RADIUS_OPTIONS.lastIndex)
        val invitationRadius = INVITATION_RADIUS_OPTIONS[invitationSelectorPosition]

        eventLoad.visibility = View.VISIBLE

        val startDateString = getFormattedDateString(startTimePicker.hour, startTimePicker.minute)
        val endDateString = getFormattedDateString(endTimePicker.hour, endTimePicker.minute)

        viewModel.createEvent(title = title,
            description = description,
            gender = selectedGender,
            icon = selectedIcon,
            event_starts = startDateString,
            event_ends = endDateString,
            latLng = userLatLng!!,
            max_radius = invitationRadius)
    }


    private fun getFormattedDateString(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Get timezone offset in "+/-HH:mm" format
        val timeZone = calendar.timeZone
        val offsetInMillis = timeZone.getOffset(calendar.timeInMillis)
        val offsetHours = offsetInMillis / (1000 * 60 * 60)
        val offsetMinutes = (offsetInMillis % (1000 * 60 * 60)) / (1000 * 60)
        val timeZoneOffset = String.format(Locale.US, "%+03d:%02d", offsetHours, Math.abs(offsetMinutes))

        return String.format(Locale.US, "%04d-%02d-%02dT%02d:%02d:00%s", year, month, day, hour, minute, timeZoneOffset)
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
                R.id.drinks -> setIcon(R.drawable.activity_drinks, getString(R.string.default_text_drinks))
                R.id.bike -> setIcon(R.drawable.activity_bike, getString(R.string.default_text_bike))
                R.id.eating -> setIcon(R.drawable.activity_eating, getString(R.string.default_text_eating))
                R.id.sports -> setIcon(R.drawable.activity_sports, getString(R.string.default_text_sports))
                R.id.walk -> setIcon(R.drawable.activity_walk, getString(R.string.default_text_walk))
                R.id.videogame -> setIcon(R.drawable.activity_videogame, getString(R.string.default_text_videogame))
                R.id.coffee -> setIcon(R.drawable.activity_coffee, getString(R.string.default_text_coffee))
                R.id.other_activity -> setIcon(R.drawable.activity_other, getString(R.string.default_text_other_activity))
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
