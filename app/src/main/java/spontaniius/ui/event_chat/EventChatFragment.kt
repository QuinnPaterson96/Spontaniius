package spontaniius.ui.event_chat


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.domain.models.User
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EventChatFragment : Fragment() {
    private val viewModel: EventChatViewModel by viewModels()

    lateinit var messagesRef: DatabaseReference
    lateinit var listOfMessages: RecyclerView
    lateinit var sendMessageButton: Button
    lateinit var input: EditText
    private var chatAdapter: FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder>? = null
    lateinit var backButton: Button
    lateinit var currentUser: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_event_chat, container, false)

        val eventId: Int = arguments?.getInt("eventId") ?: 0
        messagesRef = FirebaseDatabase.getInstance().getReference(eventId.toString())
        Log.d("ChatFragment", "Event ID: $eventId")


        backButton = view.findViewById(R.id.button_back)

        listOfMessages = view.findViewById(R.id.list_of_chat_messages)
        listOfMessages.layoutManager = LinearLayoutManager(requireContext()) // Add this line

        sendMessageButton = view.findViewById(R.id.send_message_button)
        input = view.findViewById(R.id.edit_text_message)


        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        displayChatMessages()

        sendMessageButton.setOnClickListener {
            sendMessage()
        }


        viewModel.userDetails.observe(viewLifecycleOwner){ user ->
            if (user != null) {
                currentUser = user
            }else{
                Log.e("Chat Fragment", "Error Retrieving User")
            }

        }

        viewModel.getUserDetails()
        return view
    }


    override fun onStart() {
        super.onStart()
        chatAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatAdapter?.stopListening()
    }


    private fun displayChatMessages() {
        val query = messagesRef.orderByChild("messageTime")

        val options = FirebaseRecyclerOptions.Builder<ChatMessage>()
            .setQuery(query, ChatMessage::class.java)
            .build()

        chatAdapter = object : FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.message, parent, false)
                return ChatViewHolder(view)
            }

            override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: ChatMessage) {
                holder.bind(model)
            }
        }

        listOfMessages.adapter = chatAdapter
    }

    private fun sendMessage() {
        if (::currentUser.isInitialized) {
            val message = ChatMessage(input.text.toString(), currentUser.name)

            messagesRef.push().setValue(message)
                .addOnFailureListener { e ->
                    Log.e("FirebaseSend", "Failed to send message", e)
                }

            input.setText("")
        } else {
            Log.e("FirebaseSend", "User not initialized, cannot send message")
        }
    }



    // ViewHolder Class
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)
        private val messageUser: TextView = itemView.findViewById(R.id.message_user)
        private val messageTime: TextView = itemView.findViewById(R.id.message_time)

        fun bind(chatMessage: ChatMessage) {
            messageText.text = chatMessage.messageText
            messageUser.text = chatMessage.messageUser
            messageTime.text = convertLongToTime(chatMessage.messageTime)

            // If specific messages trigger an update
            if (chatMessage.messageText!!.contains("Hey, I'm coming to join in") ||
                chatMessage.messageText!!.contains("Hope everyone had a good time, this event has ended")
            ) {
                // #TODO Fix this later   eventUpdate()
            }

        }

        // Function to convert timestamp to readable format
        private fun convertLongToTime(time: Long): String { // ViewHolder Class
            class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                private val messageText: TextView = itemView.findViewById(R.id.message_text)
                private val messageUser: TextView = itemView.findViewById(R.id.message_user)
                private val messageTime: TextView = itemView.findViewById(R.id.message_time)

                fun bind(chatMessage: ChatMessage) {
                    messageText.text = chatMessage.messageText
                    messageUser.text = chatMessage.messageUser
                    messageTime.text = convertLongToTime(chatMessage.messageTime)

                    // If specific messages trigger an update
                    if (chatMessage.messageText!!.contains("Hey, I'm coming to join in") ||
                        chatMessage.messageText!!.contains("Hope everyone had a good time, this event has ended")
                    ) {
                        // #TODO Fix this later   eventUpdate()
                    }

                }

                // Function to convert timestamp to readable format
                private fun convertLongToTime(time: Long): String {
                    val date = Date(time)
                    val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    return format.format(date)
                }
            }
            val date = Date(time)
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return format.format(date)
        }
    }
}
