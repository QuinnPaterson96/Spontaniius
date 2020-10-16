package com.example.spontaniius.ui.create_event

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spontaniius.R
import com.example.spontaniius.SpontaniiusApplication
import com.example.spontaniius.data.EventEntity
import com.example.spontaniius.data.Repository
import com.example.spontaniius.dependency_injection.CreateEventComponent
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@Deprecated("This activity has been replaced by BottomNavigationActivity (Or MainActivity, if it has been renamed)")
class CreateEventActivity :
    AppCompatActivity(),
    CreateEventFragment.OnCreateEventFragmentInteractionListener,
    MapsFragment.MapsInteractionListener {

    private val createEventFragmentTag = "CREATE EVENT TAG"
    private val mapsFragmentTag = "MAPS TAG"
    private var latLng: LatLng? = null

    @Inject
    lateinit var repository: Repository
    private lateinit var createEventComponent: CreateEventComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createEventComponent =
            (applicationContext as SpontaniiusApplication).applicationComponent.createEventComponent()
                .create()
//        createEventComponent.inject(this)
        setContentView(R.layout.activity_create_event)
        setTitle(R.string.create_event_title)
        supportFragmentManager.beginTransaction().add(
            R.id.create_event_container,
            CreateEventFragment.newInstance(),
            createEventFragmentTag
        ).commit()
    }

    override fun selectLocation() {
        val currentFragment = supportFragmentManager.findFragmentByTag(createEventFragmentTag)
        if (currentFragment != null) {
            supportFragmentManager.beginTransaction().hide(currentFragment)
                .add(R.id.create_event_container, MapsFragment(), mapsFragmentTag).commit()
        }
    }

    override fun onLocationSelected(latLng: LatLng) {
        this.latLng = latLng
        val mapsFragment = supportFragmentManager.findFragmentByTag(mapsFragmentTag)
        val createEventFragment = supportFragmentManager.findFragmentByTag(createEventFragmentTag)
        if (mapsFragment != null && createEventFragment != null) {
            supportFragmentManager.beginTransaction().remove(mapsFragment)
                .show(createEventFragment).commit()
        }
        Toast.makeText(
            this,
            "latitude is " + latLng.latitude + " and longitude is " + latLng.longitude,
            Toast.LENGTH_LONG
        ).show()
    }

    override fun createEvent(
        title: String,
        description: String,
        icon: String,
        startTime: String,
        endTime: String,
        gender: String,
        invitation: Int
    ) {
        val latLong = latLng
        if (latLong == null) {
            Toast.makeText(this, R.string.warning_no_location, Toast.LENGTH_LONG).show()
        } else {
            GlobalScope.launch {
                repository.saveEvent(
                    EventEntity(
                        title,
                        description,
                        gender,
                        "Null address",
                        "null icon",
                        startTime,
                        endTime,
                        latLong.latitude,
                        latLong.longitude,
                        invitation
                    )
                )
            }
        }
    }


}
