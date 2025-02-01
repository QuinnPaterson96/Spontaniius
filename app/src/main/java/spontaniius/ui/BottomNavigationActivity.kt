package spontaniius.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar


import com.amplifyframework.core.Amplify
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.amplifyframework.AmplifyException
import com.amplifyframework.core.AmplifyConfiguration

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.common.UserViewModel
import spontaniius.data.EventEntity
import spontaniius.di.VolleySingleton
import spontaniius.ui.card_collection.CardCollectionFragment
import spontaniius.ui.create_event.CreateEventFragment
import spontaniius.ui.create_event.MapsFragment
import spontaniius.ui.event_management.EventManagementFragment
import spontaniius.ui.find_event.FindEventFragment
import spontaniius.ui.user_menu.UserOptionsActivity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.logging.Logger


@AndroidEntryPoint
class BottomNavigationActivity : AppCompatActivity(),
    CreateEventFragment.OnCreateEventFragmentInteractionListener,
    EventManagementFragment.OnEventManagementFragmentInteractionListener,
    FindEventFragment.OnFindEventFragmentInteractionListener,
    CardCollectionFragment.OnCardCollectionFragmentInteractionListener,
    MapsFragment.MapsInteractionListener {
    private val userViewModel: UserViewModel by viewModels()

    private val mapsFragmentTag = "MAPS TAG"
    private var latLng: LatLng? = null
    lateinit var bottomNavigation: BottomNavigationView
    lateinit var currentFragment: Fragment
    lateinit var previousFragment: Fragment
    lateinit var createEventFragment: CreateEventFragment
    lateinit var eventManagementFragment: EventManagementFragment
    lateinit var findEventFragment: FindEventFragment
    lateinit var optionsMenu: ImageView
    lateinit var actionBarView:View
    lateinit var appContext: Context
    lateinit var currentEvent: JSONObject
    var meetupOwner = false
    var eventid = 0
    var eventEnds = ""
    var userDetails: JSONObject? = null

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        navController = navHostFragment?.navController ?: throw IllegalStateException("NavController not found")

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setupWithNavController(navController!!)

        // User authentication handling
        userViewModel.userAttributes.observe(this) { attributes ->
            if (attributes == null) {
                navController!!.navigate(R.id.loginFragment)
            } else {
                userDetails = attributes
            }
        }

        userViewModel.fetchUserAttributes()


        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.spontaniius_action_bar)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(0))
        actionBarView = supportActionBar!!.customView
        optionsMenu = actionBarView.findViewById(R.id.main_menu)
        appContext = this

        optionsMenu.setOnClickListener {
            val popup = PopupMenu(this, optionsMenu)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.main_menu, popup.menu)
            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {

                    return when (item.itemId) {
                        R.id.user_details -> {
                            val intentUserDetails = Intent(appContext,
                                UserOptionsActivity::class.java).apply {

                            }
                            startActivity(intentUserDetails)
                            return true
                        }
                        R.id.edit_card -> {
/*
                            var currUserAttributes = getCurrentUserAttributes()
                            val intentUserDetails =
                                Intent(appContext, CardEditingActivity::class.java).apply {
                                    if (currUserAttributes != null) {
                                        putExtra(USER_NAME, currUserAttributes.get("name").toString())
                                    }
                                    if (currUserAttributes != null) {
                                        putExtra(PHONE_NUMBER,
                                            currUserAttributes.get("phone_number").toString())
                                    }
                                    putExtra(USER_ID, "spoof") //#TODO fix this
                                    putExtra(CARD_EDIT_NEW, false)
                                }

                            startActivity(intentUserDetails)*/
                            return true
                        }
                        R.id.about_us -> {
                            val openURL = Intent(android.content.Intent.ACTION_VIEW)
                            openURL.data = Uri.parse("https://www.spontaniius.com")
                            startActivity(openURL)
                            return true
                        }

                        R.id.report_an_issue ->{
                            val openURL = Intent(android.content.Intent.ACTION_VIEW)
                            openURL.data = Uri.parse("https://www.spontaniius.com/contact")
                            startActivity(openURL)
                            return true
                        }
/*                        R.id.sign_out -> {
                            Amplify.Auth.signOut(
                                AuthSignOutOptions.builder().globalSignOut(true).build(),
                                {
                                    Log.i(
                                        "AuthQuickstart",
                                        "Signed out globally"
                                    )
                                }
                            ) { error: com.amplifyframework.auth.AuthException ->
                                Log.e(
                                    "AuthQuickstart",
                                    error.toString()
                                )
                            }
                            val intentSignout =
                                Intent(appContext, SignUpActivity::class.java).apply {

                                }
                            startActivity(intentSignout)
                            return true
                        }*/
                        else -> false
                    }
                }
            })
            popup.show()
        }
    }

    override fun onBackPressed() {
        navController?.popBackStack()
    }

    override fun onLocationSelected(latLng: LatLng) {
        this.latLng = latLng
        navController?.navigate(R.id.createEventFragment)
        Toast.makeText(
            this,
            "latitude is " + latLng.latitude + " and longitude is " + latLng.longitude,
            Toast.LENGTH_LONG
        ).show()
    }


    override fun selectLocation() {
        navController?.navigate(R.id.mapsFragment)
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
        invitation: Int,
        cardId: Int,
        loadingProgress: ProgressBar // Kludge to get around not being able to make work on resume
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
                invitation,
                cardId
            )

            currentEvent = thisEvent.toJSON()
            val url = "https://217wfuhnk6.execute-api.us-west-2.amazonaws.com/default/createSpontaniiusEvent"
            val getLocationRequest = JsonObjectRequest(
                Request.Method.POST, url, thisEvent.toJSON(),
                { response ->
                    val JSONResponse = JSONObject(response.toString())
                    eventid = JSONResponse.get("eventid") as Int
                    meetupOwner = true
                    loadingProgress.visibility=GONE

                    val bundle = Bundle().apply {
                        putString("event_id", eventid.toString())
                        putBoolean("is_event_owner", meetupOwner)
                    }
                    navController?.navigate(R.id.eventManagementFragment, bundle)
                },
                { error ->
                    loadingProgress.visibility=GONE
                    error.printStackTrace()
                }

            )
            val queue = this.let { VolleySingleton.getInstance(it).requestQueue }
            queue.add(getLocationRequest)
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
            { _ ->
                navController?.navigate(R.id.findEventFragment)
                meetupOwner = false
            },
            { error ->
                error.printStackTrace()
                //#TODO add logging
            }
        )
        val queue = this.let { VolleySingleton.getInstance(it).requestQueue }
        queue.add(endEventRequest)
    }

    override fun add15Mins(){
        val newEndTime = ZonedDateTime.parse(
            eventEnds as CharSequence?, DateTimeFormatter.ofPattern(
                "yyyy-[M][MM]-[d][dd]['T'][ ][HH][H]:[m][mm]:ssz"
            )
        )

        val eventEndsTime = newEndTime.plusMinutes(15)


        eventEnds = eventEndsTime.format(DateTimeFormatter.ofPattern(
            "yyyy-M-dd HH:mm:ssz"
        )).toString()



        val url =
            "https://217wfuhnk6.execute-api.us-west-2.amazonaws.com/default/createSpontaniiusEvent?eventid=$eventid&newEndTime=$eventEnds";
        val extendEventRequest = StringRequest(Request.Method.PUT, url,
            { response ->
                Toast.makeText(
                    this,
                    "Event extended by 15 mins",
                    Toast.LENGTH_LONG
                ).show()
            },
            { error ->
                error.printStackTrace()
            }
        )
        val queue = this?.let { VolleySingleton.getInstance(it).requestQueue }
        queue?.add(extendEventRequest)
        }



    override fun openEventChatroom(eventid: String, event: JSONObject) {
        val bundle = Bundle().apply {
            putString("event_id", eventid)
            putBoolean("is_event_owner", meetupOwner)
        }
        navController?.navigate(R.id.eventManagementFragment, bundle)
    }

    override fun switchToCreate() {
        navController?.navigate(R.id.createEventFragment)

    }

    override fun exitEvent(){
        navController?.navigate(R.id.findEventFragment)
    }

    override fun getEventID(): Int {
        return eventid
    }

    override fun whatIsCurrentEvent():JSONObject{
        return currentEvent
    }

    // This was created to streamline the process of accessing user attributes and to reduce code
    // duplication across program. It fetches the user attributes and returns them as a JSON object
    // If the details have already been fetched it avoids calling AWS Auth again

    override fun getCurrentUserAttributes(): JSONObject? {
         return userDetails
       }

}


