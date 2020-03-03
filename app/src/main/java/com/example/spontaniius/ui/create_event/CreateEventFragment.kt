package com.example.spontaniius.ui.create_event

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.spontaniius.R
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CreateEventFragment.OnCreateEventFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CreateEventFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateEventFragment : Fragment() {
    private var listenerCreateEvent: OnCreateEventFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewLayout = inflater.inflate(R.layout.fragment_create_event, container, false)
        val iconSelectButton = viewLayout.findViewById<ImageButton>(R.id.event_icon)
        iconSelectButton.setOnClickListener{
            listenerCreateEvent?.selectEventIcon()
//            TODO("Make the image that the user selects the icon here")
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
        ).also {arrayAdapter ->  
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            genderSpinner.adapter = arrayAdapter
        }
        val createEventButton = viewLayout.findViewById<Button>(R.id.create_event_button)
        createEventButton.setOnClickListener{
            val title = viewLayout.findViewById<EditText>(R.id.event_title)
            val description = viewLayout.findViewById<EditText>(R.id.event_description)
//            TODO: Location
            val startTime = viewLayout.findViewById<TimePicker>(R.id.event_start_time_picker)
            val endTime= viewLayout.findViewById<TimePicker>(R.id.event_end_time_picker)
            when {
                title.text.toString() == "" -> {
                    Toast.makeText(context, getString(R.string.warning_input_title), Toast.LENGTH_LONG).show()
                }
                description.text.toString() == "" -> {
                    Toast.makeText(context, R.string.warning_input_description, Toast.LENGTH_LONG).show()
                }

                else -> {
                    val startDate = Calendar.getInstance()
                    startDate.set(Calendar.HOUR, startTime.hour)
                    startDate.set(Calendar.MINUTE, startTime.minute)
                    val endDate = Calendar.getInstance()
                    endDate.set(Calendar.HOUR, endTime.hour)
                    endDate.set(Calendar.MINUTE, endTime.minute)
                    listenerCreateEvent?.createEvent(
                        title.text.toString(),
                        description.text.toString(),
                        startDate.timeInMillis,
                        endDate.timeInMillis,
                        1,
                        1,
                        1
                    )
                }
            }
        }
        return viewLayout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCreateEventFragmentInteractionListener) {
            listenerCreateEvent = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerCreateEvent = null
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
        fun selectEventIcon(): Any?
        fun selectLocation(): Any?
        fun createEvent(
            title: String,
            description: String,
            startTime: Long, //using unix timestamp
            endTime: Long,
            location: Any?,
            gender: Any?,
            invitation: Any?
        )
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment CreateEventFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            CreateEventFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
