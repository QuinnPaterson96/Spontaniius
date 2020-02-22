package com.example.spontaniius.ui.create_event

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.spontaniius.R


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
