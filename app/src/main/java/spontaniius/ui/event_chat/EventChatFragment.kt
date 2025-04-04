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

    private lateinit var messagesRef: DatabaseReference
    private lateinit var listOfMessages: RecyclerView
    private lateinit var sendMessageButton: Button
    private lateinit var input: EditText
    private lateinit var backButton: Button
    private var chatAdapter: FirebaseRecyclerAdapter<ChatMessage, ChatViewHolder>? = null

    private var currentUser: User? = null
    private var eventId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_event_chat, container, false)

        eventId = arguments?.getInt("eventId") ?: -1
        if (eventId == -1) {
            Log.e("ChatFragment", "No event ID passed")
            findNavController().popBackStack()
            return view
        }

        messagesRef = FirebaseDatabase.getInstance().getReference("chats/$eventId")
        Log.d("ChatFragment", "Using Firebase path: chats/$eventId")

        listOfMessages = view.findViewById(R.id.list_of_chat_messages)
        sendMessageButton = view.findViewById(R.id.send_message_button)
        input = view.findViewById(R.id.edit_text_message)
        backButton = view.findViewById(R.id.button_back)

        listOfMessages.layoutManager = LinearLayoutManager(requireContext())
        backButton.setOnClickListener { findNavController().popBackStack() }
        sendMessageButton.setOnClickListener { sendMessage() }

        viewModel.userDetails.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                currentUser = user
            } else {
                Log.e("ChatFragment", "User not retrieved")
            }
        }

        viewModel.getUserDetails()
        displayChatMessages()

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
                val view = LayoutInflater.from(parent.context).inflate(R.layout.message, parent, false)
                return ChatViewHolder(view)
            }

            override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: ChatMessage) {
                holder.bind(model)
            }
        }

        listOfMessages.adapter = chatAdapter

        chatAdapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                listOfMessages.scrollToPosition(chatAdapter!!.itemCount - 1)
            }
        })

    }

    private fun sendMessage() {
        val user = currentUser
        if (user != null) {
            val text = input.text.toString().trim()
            if (text.isBlank()) return

            val message = ChatMessage(text, user.name)
            Log.d("FirebaseSend", "Sending to path: ${messagesRef.path}, message: $message")

            messagesRef.push().setValue(message)
                .addOnSuccessListener {
                    Log.d("FirebaseSend", "Message sent successfully")
                    input.setText("")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseSend", "Failed to send message", e)
                }
        } else {
            Log.e("FirebaseSend", "User not initialized, cannot send message")
        }
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.message_text)
        private val messageUser: TextView = itemView.findViewById(R.id.message_user)
        private val messageTime: TextView = itemView.findViewById(R.id.message_time)

        fun bind(chatMessage: ChatMessage) {
            messageText.text = chatMessage.messageText
            messageUser.text = chatMessage.messageUser
            messageTime.text = convertLongToTime(chatMessage.messageTime)
            itemView.alpha = 0f
            itemView.animate()
                .alpha(1f)
                .setDuration(500)
                .start()

            if (chatMessage.messageText?.contains("Hey, I'm coming to join in") == true ||
                chatMessage.messageText?.contains("Hope everyone had a good time, this event has ended") == true
            ) {
                // #TODO implement eventUpdate()
            }
        }

        private fun convertLongToTime(time: Long): String {
            val date = Date(time)
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return format.format(date)
        }
    }
}
