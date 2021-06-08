package spontaniius.ui.event_management

import android.os.Bundle
import android.view.View
import android.view.View.*
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import spontaniius.R
import android.content.Intent
import android.os.Build
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.Fragment
import com.example.spontaniius.ui.promotions.FindPromotionsFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import spontaniius.SpontaniiusApplication
import spontaniius.data.EventEntity
import spontaniius.data.Repository
import spontaniius.dependency_injection.CreateEventComponent
import spontaniius.ui.create_event.CreateEventFragment
import spontaniius.ui.create_event.MapsFragment
import spontaniius.ui.event_management.EventManagementActivity
import spontaniius.ui.find_event.FindEventFragment
import spontaniius.ui.login.LoginActivity
import javax.inject.Inject



class EventManagementActivity : AppCompatActivity() {
    var database = FirebaseDatabase.getInstance()
    var myRef = database.getReference("message")
    var listOfMessages: ListView = findViewById<View>(R.id.list_of_messages) as ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_management)
        setSupportActionBar(findViewById(R.id.toolbar))

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton

        fab.setOnClickListener(sendMessage())
        // Load chat room contents
        displayChatMessages();
        }

    }

    private fun sendMessage(): View.OnClickListener? {
        val input = v.findViewById(R.id.input) as EditText
        // Read the input field and push a new instance
        // of ChatMessage to the Firebase database
        FirebaseDatabase.getInstance()
            .getReference()
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
        return null
    }
    private fun displayChatMessages() {

        val listOfMessages = findViewById<List>(R.id.list_of_messages) as ListView

        adapter = object : FirebaseListAdapter<ChatMessage?>(
            this, ChatMessage::class.java,
            R.layout.message, FirebaseDatabase.getInstance().reference
        ) {
            protected fun populateView(v: View, model: ChatMessage, position: Int) {
                // Get references to the views of message.xml
                val messageText = v.findViewById<View>(R.id.message_text) as TextView
                val messageUser = v.findViewById<View>(R.id.message_user) as TextView
                val messageTime = v.findViewById<View>(R.id.message_time) as TextView

                // Set their text
                messageText.text = model.messageText
                messageUser.text = model.messageUser

                // Format the date before showing it
                messageTime.setText(
                    DateFormat.format(
                        "dd-MM-yyyy (HH:mm:ss)",
                        model.messageTime
                    )
                )
            }
        }

        listOfMessages.adapter = adapter
    }
}