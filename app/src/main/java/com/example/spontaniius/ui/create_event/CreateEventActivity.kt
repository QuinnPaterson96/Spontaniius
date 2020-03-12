package com.example.spontaniius.ui.create_event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spontaniius.R
import com.example.spontaniius.data.DefaultRepository
import com.example.spontaniius.data.EventEntity
import com.example.spontaniius.data.Repository
import com.example.spontaniius.data.data_source.LocalDataSource
import com.example.spontaniius.data.data_source.RemoteDataSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CreateEventActivity :
    AppCompatActivity(),
    CreateEventFragment.OnCreateEventFragmentInteractionListener {

    private val createEventFragmentTag = "CREATE EVENT TAG"
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
//        TODO("refactor the following to use DAGGER")
        val localDataSource = LocalDataSource()
        val remoteDataSource = RemoteDataSource()
        repository = DefaultRepository(localDataSource, remoteDataSource)

        setupActionBar()
        supportFragmentManager.beginTransaction().add(
            R.id.create_event_container,
            CreateEventFragment.newInstance(),
            createEventFragmentTag
        ).commit()
    }


    private fun setupActionBar() {
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
            repository.saveEvent(EventEntity(title, description, icon, startTime, endTime, location, gender, invitation))
        }
    }
}
