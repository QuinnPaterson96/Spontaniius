package spontaniius.ui.event_management

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.firebase.ui.database.FirebaseListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import spontaniius.R
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "eventid"
private const val ARG_PARAM2 = "role"

/**
 * A simple [Fragment] subclass.
 * Use the [EventManagementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EventManagementFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var listenerManageEvent: EventManagementFragment.OnEventManagementFragmentInteractionListener? = null



    lateinit var myRef: DatabaseReference


    lateinit var listOfMessages: ListView
    private var adapter: FirebaseListAdapter<ChatMessage>? = null

    lateinit var fab: FloatingActionButton
    lateinit var input: EditText
    lateinit var fragmentView: View
    lateinit var endEventButton: Button
    lateinit var add15MinsButton : Button


    companion object {
        fun newInstance(param1: String, param2: Boolean): EventManagementFragment {
            return EventManagementFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putBoolean(ARG_PARAM2, param2)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        fragmentView = inflater.inflate(R.layout.fragment_event_management, container, false)

        var args = arguments
        var eventID = args!!.getString(ARG_PARAM1, "0")
        var role = args!!.getBoolean(ARG_PARAM2, false)
        myRef = FirebaseDatabase.getInstance().getReference(eventID)


        listOfMessages = fragmentView?.findViewById<View>(R.id.list_of_messages) as ListView
        fab = fragmentView?.findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            input = fragmentView?.findViewById<View>(R.id.input) as EditText

            // Read the input field and push a new instance
            // of ChatMessage to the Firebase database
            myRef
                .push()
                .setValue(
                    ChatMessage(
                        input.text.toString(),
                        FirebaseAuth.getInstance()
                            .currentUser
                            ?.getDisplayName()
                    )
                )

            // Clear the input
            input.setText("")

        }
        endEventButton = fragmentView?.findViewById<View>(R.id.endButton) as Button
        endEventButton.setOnClickListener{
            listenerManageEvent?.endEvent()
        }

        add15MinsButton = fragmentView?.findViewById<View>(R.id.addButton) as Button
        add15MinsButton.setOnClickListener{
            listenerManageEvent?.add15Mins()
        }
        // Load chat room contents
        displayChatMessages();
        return fragmentView
    }



    private fun displayChatMessages() {

        adapter = object : FirebaseListAdapter<ChatMessage>(
            activity, ChatMessage::class.java,
            R.layout.message, myRef
        ) {
            override fun populateView(v: View, model: ChatMessage, position: Int) {
                // Get references to the views of message.xml
                val messageText = v.findViewById<View>(R.id.message_text) as TextView
                val messageUser = v.findViewById<View>(R.id.message_user) as TextView
                val messageTime = v.findViewById<View>(R.id.message_time) as TextView

                // Set their text
                messageText.text = model.messageText
                messageUser.text = model.messageUser

                // Format the date before showing it
                messageTime.setText(
                    convertLongToTime(model.messageTime)
                )
            }
        }
        listOfMessages.adapter = adapter;
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EventManagementFragment.OnEventManagementFragmentInteractionListener) {
            listenerManageEvent = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }


    interface OnEventManagementFragmentInteractionListener {
        fun endEvent()
        fun add15Mins()
    }
}