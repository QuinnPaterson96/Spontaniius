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


import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.common.AuthViewModel
import spontaniius.common.UserViewModel
import spontaniius.data.EventEntity
import spontaniius.data.remote.models.UserResponse
import spontaniius.ui.card_collection.CardCollectionFragment
import spontaniius.ui.create_event.CreateEventFragment
import spontaniius.ui.create_event.MapsFragment
import spontaniius.ui.event_management.EventManagementFragment
import spontaniius.ui.find_event.FindEventFragment
import spontaniius.ui.user_menu.UserOptionsActivity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    EventManagementFragment.OnEventManagementFragmentInteractionListener,
    FindEventFragment.OnFindEventFragmentInteractionListener,
    CardCollectionFragment.OnCardCollectionFragmentInteractionListener{
    private val userViewModel: UserViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var latLng: LatLng? = null
    lateinit var optionsMenu: ImageView
    lateinit var actionBarView:View
    lateinit var appContext: Context
    lateinit var currentEvent: JSONObject
    var meetupOwner = false
    var eventid = 0
    var eventEnds = ""
    var userDetails: UserResponse? = null

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
            userDetails = attributes
        }

        authViewModel.isUserSignedIn.observe(this) { signedIn ->
            if (signedIn == true) {
                navController!!.navigate(R.id.findEventFragment)
            } else {
                navController!!.navigate(R.id.loginFragment)
                userViewModel.refreshUserAttributes()
            }
        }

        authViewModel.checkAuthState()



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
                        R.id.sign_out -> {
                            authViewModel.signOut()
                            return true
                        }
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


