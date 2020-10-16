package com.example.spontaniius.ui

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.spontaniius.R
import com.example.spontaniius.SpontaniiusApplication
import com.example.spontaniius.data.EventEntity
import com.example.spontaniius.data.Repository
import com.example.spontaniius.dependency_injection.CreateEventComponent
import com.example.spontaniius.ui.create_event.CreateEventFragment
import com.example.spontaniius.ui.create_event.MapsFragment
import com.example.spontaniius.ui.find_event.FindEventFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO: rename to MainActivity
class BottomNavigationActivity : AppCompatActivity(),
    CreateEventFragment.OnCreateEventFragmentInteractionListener,
    MapsFragment.MapsInteractionListener {

    private val createEventFragmentTag = "CREATE EVENT TAG"
    private val mapsFragmentTag = "MAPS TAG"
    private var latLng: LatLng? = null

    @Inject
    lateinit var repository: Repository
    private lateinit var createEventComponent: CreateEventComponent

    lateinit var bottomNavigation: BottomNavigationView
    lateinit var fragment_container: FrameLayout
    lateinit var currentFragment: Fragment
    lateinit var createEventFragment: CreateEventFragment
    lateinit var promotionFragment: Fragment
    lateinit var findEventFragment: FindEventFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)

        createEventComponent =
            (applicationContext as SpontaniiusApplication).applicationComponent.createEventComponent()
                .create()
        createEventComponent.inject(this)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        fragment_container = findViewById(R.id.fragment_container)
        createEventFragment = CreateEventFragment.newInstance()
        findEventFragment = FindEventFragment.newInstance()

        //TODO: change the fragment to represent what it actually needs to do
        promotionFragment = FindEventFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, promotionFragment, null).hide(promotionFragment).commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, createEventFragment, null).hide(createEventFragment)
            .commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, findEventFragment, null).commit()
        currentFragment = findEventFragment
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.find_event -> {
                    supportFragmentManager.beginTransaction().hide(currentFragment)
                        .show(findEventFragment).commit()
                    currentFragment = findEventFragment
                    true
                }
                R.id.create_event -> {
                    supportFragmentManager.beginTransaction().hide(currentFragment)
                        .show(createEventFragment).commit()
                    currentFragment = createEventFragment
                    true
                }
                R.id.promotions -> {
                    supportFragmentManager.beginTransaction().hide(currentFragment)
                        .show(promotionFragment).commit()
                    currentFragment = promotionFragment
                    true
                }
                else -> {
                    false
                }
            }
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


    override fun selectLocation() {
        supportFragmentManager.beginTransaction().hide(currentFragment)
            .add(R.id.create_event_container, MapsFragment(), mapsFragmentTag).commit()
//        TODO: Fix this, it no longer works
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