package spontaniius.ui.create_event

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import com.spontaniius.R
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import spontaniius.common.PlacesViewModel
import spontaniius.data.remote.models.PlaceSuggestion
import java.time.LocalTime
import java.util.*

val LOCATION_PERMISSION_REQUEST_CODE = 1
val INVITATION_RADIUS_OPTIONS = listOf(500, 2000, 5000) // Corresponding radius values


@AndroidEntryPoint
class CreateEventFragment : Fragment(), MapsFragment.MapsInteractionListener {

    private val viewModel: CreateEventViewModel by viewModels()
    private val placesViewModel: PlacesViewModel by viewModels()

    private lateinit var eventLoad: ProgressBar
    private lateinit var titleField: EditText
    private lateinit var descriptionField: EditText
    private lateinit var iconSelectButton: ImageButton
    private lateinit var invitationTypePicker: RadioGroup
    private lateinit var selectedButton: RadioButton
    private lateinit var startTimeText: TextInputEditText
    private lateinit var endTimeText: TextInputEditText



    private var selectedGender: String = "Any"
    private var selectedIcon: String = ""
    private var userLatLng: LatLng? = null
    private var stockTitle = false
    private var isSettingText = false
    private var selectedStartTime: LocalTime = LocalTime.now()
    private var selectedEndTime: LocalTime = LocalTime.now().plusHours(1)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val viewLayout = inflater.inflate(R.layout.fragment_create_event, container, false)

        // Initialize views
        titleField = viewLayout.findViewById(R.id.event_title)
        descriptionField = viewLayout.findViewById(R.id.event_description)
        eventLoad = viewLayout.findViewById(R.id.loading)
        iconSelectButton = viewLayout.findViewById(R.id.event_icon)
        startTimeText = viewLayout.findViewById(R.id.event_start_time_picker)
        endTimeText = viewLayout.findViewById(R.id.event_end_time_picker)


        startTimeText.setText(formatTime(selectedStartTime))
        startTimeText.setOnClickListener {
            showTimePickerDialog(true)
        }

        // Set up End Time Picker
        endTimeText.setText(formatTime(selectedEndTime))
        endTimeText.setOnClickListener {
            showTimePickerDialog(false)
        }


        val searchEditText = viewLayout.findViewById<EditText>(R.id.locationSearchEditText)
        val suggestionsListView = viewLayout.findViewById<ListView>(R.id.suggestionsList)


        val locateMeButton = viewLayout.findViewById<ImageView>(R.id.locate_me_button_create);
        locateMeButton.setOnClickListener {
            viewModel.getCurrentLocation()
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                placesViewModel.places.collectLatest { places ->
                    if(places!=null){
                        updateSuggestionsList(places, suggestionsListView, searchEditText)
                    }
                }
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isSettingText){
                    placesViewModel.searchPlaces(s.toString(), location = userLatLng, apiKey = getString(R.string.google_api_key))  // Calls Places API
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        placesViewModel.placeDetails.observe(viewLifecycleOwner){ placeDetails ->
            if (placeDetails != null) {
                isSettingText = true
                searchEditText.setText(placeDetails.formattedAddress)
                userLatLng = LatLng(placeDetails.location.latitude, placeDetails.location.longitude)
                isSettingText = false
            }
        }


        viewModel.address.observe(viewLifecycleOwner){ address ->
            if (address!=null){
                isSettingText = true
                searchEditText.setText(address)
                isSettingText = false
            }
        }

        viewModel.getCurrentLocation()

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
        /*
        //val selectedId = invitationTypePicker.checkedRadioButtonId

        val selectedRadioButton: RadioButton = (invitationTypePicker.findViewById<RadioButton>(selectedId))
        val invitationSelectorPosition = invitationTypePicker.indexOfChild(selectedRadioButton).coerceIn(0, INVITATION_RADIUS_OPTIONS.lastIndex)
        val invitationRadius = INVITATION_RADIUS_OPTIONS[invitationSelectorPosition] # Deprecated radius buttons
         */
        eventLoad.visibility = View.VISIBLE


        viewModel.createEvent(title = title,
            description = description,
            gender = selectedGender,
            icon = selectedIcon,
            event_starts = getFormattedDateString(selectedStartTime.hour, selectedStartTime.minute),
            event_ends = getFormattedDateString(selectedEndTime.hour, selectedEndTime.minute),
            latLng = userLatLng!!,
            max_radius = 5000)
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
                R.id.boardgames -> setIcon(R.drawable.activity_boardgames, getString(R.string.default_text_boardgames))
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

    fun updateSuggestionsList(places: List<PlaceSuggestion>, listView: ListView, searchEditText: EditText) {

        if (places.isNotEmpty()){
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, places.map { it.placePrediction.text_field.fullText })
            listView.adapter = adapter
            listView.visibility = View.VISIBLE

            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedPlace = places[position]
                Log.i("FindEventFragment", "Selected Place: ${selectedPlace.placePrediction.text_field.fullText}")

                // Hide the list & update EditText
                listView.visibility = View.GONE
                isSettingText=true
                searchEditText.setText(selectedPlace.placePrediction.text_field.fullText)
                isSettingText=false
                placesViewModel.getPlaceDetails(selectedPlace.placePrediction.placeId, getString(R.string.google_api_key))
            }
        }else{
            listView.visibility = View.GONE
        }
    }

    private fun showTimePickerDialog(isStartTime: Boolean) {
        val initialTime = if (isStartTime) selectedStartTime else selectedEndTime

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(initialTime.hour)
            .setMinute(initialTime.minute)
            .setTitleText(if (isStartTime) "Select Start Time" else "Select End Time")
            .build()

        picker.show(parentFragmentManager, "timePicker")

        picker.addOnPositiveButtonClickListener {
            val newTime = LocalTime.of(picker.hour, picker.minute)
            val timeString = formatTime(newTime)

            if (isStartTime) {
                selectedStartTime = newTime
                startTimeText.setText(timeString)
            } else {
                selectedEndTime = newTime
                endTimeText.setText(timeString)
            }
        }
    }
    private fun formatTime(time: LocalTime): String {
        return String.format(Locale.US, "%02d:%02d", time.hour, time.minute)
    }

}
