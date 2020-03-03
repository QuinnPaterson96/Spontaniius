package com.example.spontaniius.ui.create_event

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spontaniius.R

class CreateEventActivity :
    AppCompatActivity(),
    CreateEventFragment.OnCreateEventFragmentInteractionListener {

    private val createEventFragmentTag = "CREATE EVENT TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        setupActionBar()
        supportFragmentManager.beginTransaction().add(
            R.id.create_event_container,
            CreateEventFragment.newInstance(),
            createEventFragmentTag
        ).commit()
    }

    private fun setupActionBar(){
        actionBar?.title = getString(R.string.create_event_title)
    }

    override fun selectEventIcon(): Any? {
        Toast.makeText(this, "Button tapped", Toast.LENGTH_LONG).show()
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return -1
    }

    override fun selectLocation(): Any? {
        val currentFragment = supportFragmentManager.findFragmentByTag(createEventFragmentTag)
        if (currentFragment != null) {
            supportFragmentManager.beginTransaction().hide(currentFragment)
                .add(R.id.create_event_container, MapsFragment()).commit()
        }
        return -1
    }

    override fun createEvent(
        title: String,
        description: String,
        startTime: Long,
        endTime: Long,
        location: Any?,
        gender: Any?,
        invitation: Any?
    ) {

//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
