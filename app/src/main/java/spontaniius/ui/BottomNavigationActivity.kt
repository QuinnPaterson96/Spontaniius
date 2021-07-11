package spontaniius.ui

import android.os.Build
import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.spontaniius.ui.promotions.FindPromotionsFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import spontaniius.R
import spontaniius.SpontaniiusApplication
import spontaniius.data.EventEntity
import spontaniius.data.Repository
import spontaniius.dependency_injection.CreateEventComponent
import spontaniius.dependency_injection.VolleySingleton
import spontaniius.ui.create_event.CreateEventFragment
import spontaniius.ui.create_event.MapsFragment
import spontaniius.ui.event_management.EventManagementFragment
import spontaniius.ui.find_event.EventTile
import spontaniius.ui.find_event.FindEventFragment
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


//TODO: rename to MainActivity
class BottomNavigationActivity : AppCompatActivity(),
    CreateEventFragment.OnCreateEventFragmentInteractionListener,
    EventManagementFragment.OnEventManagementFragmentInteractionListener,
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
    lateinit var eventManagementFragment: EventManagementFragment

    lateinit var findEventFragment: FindEventFragment
    lateinit var iconSelectButton: ImageButton
    var meetupOwner = false
    var eventid = 0
    var eventEnds = ""

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
        eventManagementFragment = EventManagementFragment.newInstance("1", "Manager")
        //eventManagementFragment = EventManagementFragment.newInstance()


        //TODO: when Cord is done with his promotion fragment, create that here
//        TODO: Then, have this class (BottomNavigationActivity) implement all the methods that the fragment calls (probably want to define an interface for that)
//            Note: the above can be accomplished with a lil copy-paste
//        TODO: mark old classes as deprecated (see CreateEventActivity.kt for an example of how to do that)
//        After the refactoring has been stable for a few commits, feel free to remove the old classes entirely (git has records of them, if they are really needed)
        promotionFragment = FindPromotionsFragment.newInstance()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, eventManagementFragment, null).hide(eventManagementFragment).commit()
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
                    if(meetupOwner){
                        supportFragmentManager.beginTransaction().hide(currentFragment)
                            .show(eventManagementFragment).commit()
                        currentFragment = eventManagementFragment
                        true
                    }else{
                        supportFragmentManager.beginTransaction().hide(currentFragment)
                            .show(createEventFragment).commit()
                        currentFragment = createEventFragment
                        true
                    }


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
        if (mapsFragment != null) {
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
            .add(R.id.fragment_container, MapsFragment(), mapsFragmentTag).commit()
    }

    override fun googleLocationSelect(latLng: LatLng) {
        this.latLng = latLng
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


        eventEnds = endTime
        val latLong = latLng
        if (latLong == null) {
            Toast.makeText(this, R.string.warning_no_location, Toast.LENGTH_LONG).show()
        } else {
            var thisEvent = EventEntity(
                title,
                description,
                gender,
                "Null address",
                icon,
                startTime,
                endTime,
                latLong.latitude,
                latLong.longitude,
                invitation
            )

            val url = "https://217wfuhnk6.execute-api.us-west-2.amazonaws.com/default/createSpontaniiusEvent"
            val getLocationRequest = JsonObjectRequest(
                Request.Method.POST, url, thisEvent.toJSON(),
                Response.Listener<JSONObject> { response ->
                    val JSONResponse = JSONObject(response.toString())
                    eventid = JSONResponse.get("eventid") as Int
                    supportFragmentManager.beginTransaction().hide(currentFragment)
                        .show(eventManagementFragment).commitNow()
                    currentFragment = eventManagementFragment
                    meetupOwner = true

                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                }

            )
            val queue = this?.let { VolleySingleton.getInstance(it).requestQueue }
            queue?.add(getLocationRequest)
            /*
           GlobalScope.launch {
                repository.saveEvent(
                    EventEntity(
                        title,
                        description,
                        gender,
                        "Null address",
                        icon,
                        startTime,
                        endTime,
                        latLong.latitude,
                        latLong.longitude,
                        invitation
                    )
                )
           }
            Toast.makeText(
                this,
                "Event Created",
                Toast.LENGTH_LONG
            ).show()
            */


        }
    }
    override fun endEvent(){
        val currtime = Calendar.getInstance().time
        val url =
            "https://217wfuhnk6.execute-api.us-west-2.amazonaws.com/default/createSpontaniiusEvent?eventid=$eventid&newEndTime="+currtime;
        val endEventRequest = StringRequest(Request.Method.PUT, url,
            { response ->
                meetupOwner = false
                eventid = 0
                supportFragmentManager.beginTransaction().hide(currentFragment)
                    .show(createEventFragment).commit()
                currentFragment = createEventFragment
            },
            { error ->
                error.printStackTrace()
            }
        )
        val queue = this?.let { VolleySingleton.getInstance(it).requestQueue }
        queue?.add(endEventRequest)
    }

    override fun add15Mins(){
        var newEndTime = ZonedDateTime.parse(
            eventEnds as CharSequence?, DateTimeFormatter.ofPattern(
                "yyyy-M-dd HH:mm:ssz"
            )
        )

        var eventEndsTime = newEndTime.plusMinutes(15)


        eventEnds = eventEndsTime.format(DateTimeFormatter.ofPattern(
            "yyyy-M-dd HH:mm:ssz"
        )).toString()





        val url ="https://217wfuhnk6.execute-api.us-west-2.amazonaws.com/default/createSpontaniiusEvent?eventid=$eventid&newEndTime="+eventEnds;
        val extendEventRequest = StringRequest(Request.Method.PUT, url,
            { response ->
                meetupOwner = false
                eventid = 0
                supportFragmentManager.beginTransaction().hide(currentFragment)
                    .show(createEventFragment).commit()
                currentFragment = createEventFragment
            },
            { error ->
                error.printStackTrace()
            }
        )
        val queue = this?.let { VolleySingleton.getInstance(it).requestQueue }
        queue?.add(extendEventRequest)
        }
    }

