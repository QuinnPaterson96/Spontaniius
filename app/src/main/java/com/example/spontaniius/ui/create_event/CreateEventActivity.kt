package com.example.spontaniius.ui.create_event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spontaniius.R
import com.example.spontaniius.SpontaniiusApplication
import com.example.spontaniius.data.EventEntity
import com.example.spontaniius.data.Repository
import com.example.spontaniius.dependency_injection.CreateEventComponent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateEventActivity :
    AppCompatActivity(),
    CreateEventFragment.OnCreateEventFragmentInteractionListener {

    private val createEventFragmentTag = "CREATE EVENT TAG"

    @Inject
    lateinit var repository: Repository
    private lateinit var createEventComponent: CreateEventComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createEventComponent =
            (applicationContext as SpontaniiusApplication).applicationComponent.createEventComponent()
                .create()
        createEventComponent.inject(this)
        setContentView(R.layout.activity_create_event)
        setupActionBar()
        supportFragmentManager.beginTransaction().add(
            R.id.create_event_container,
            CreateEventFragment.newInstance(),
            createEventFragmentTag
        ).commit()
    }


    private fun setupActionBar() {
//        TODO: Debug
        actionBar?.title = getString(R.string.create_event_title)
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
        icon: String,
        startTime: Long,
        endTime: Long,
        location: Any?,
        gender: String,
        invitation: Int
    ) {
        GlobalScope.launch {
            repository.saveEvent(
                EventEntity(
//                    IntArray(0),
                    title,
                    description,
                    gender,
                    "Null address",
                    "null icon",
                    startTime,
                    endTime,
                    invitation
                )
            )
        }
    }
}
