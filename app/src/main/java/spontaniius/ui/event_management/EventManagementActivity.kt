package spontaniius.ui.event_management

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.database.FirebaseListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import spontaniius.R
import java.text.SimpleDateFormat
import java.util.*


class EventManagementActivity : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("message")
    lateinit var listOfMessages: ListView
    private var adapter: FirebaseListAdapter<ChatMessage>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_management)

        listOfMessages = findViewById<View>(R.id.list_of_messages) as ListView
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            val input = findViewById<View>(R.id.input) as EditText

            // Read the input field and push a new instance
            // of ChatMessage to the Firebase database
            FirebaseDatabase.getInstance()
                .reference
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
        // Load chat room contents
        displayChatMessages();
    }


    private fun displayChatMessages() {

        adapter = object : FirebaseListAdapter<ChatMessage>(
            this, ChatMessage::class.java,
            R.layout.message, FirebaseDatabase.getInstance().reference
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
}