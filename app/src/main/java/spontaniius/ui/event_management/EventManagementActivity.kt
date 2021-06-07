package spontaniius.ui.event_management

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import spontaniius.R

class EventManagementActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_management)
        setSupportActionBar(findViewById(R.id.toolbar))

    }

    private fun displayChatMessages() {}
}