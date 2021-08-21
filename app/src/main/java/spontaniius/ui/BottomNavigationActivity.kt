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
import androidx.appcompat.app.ActionBar


import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.core.Amplify
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.spontaniius.ui.promotions.FindPromotionsFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONException
import org.json.JSONObject
import spontaniius.R
import spontaniius.SpontaniiusApplication
import spontaniius.data.EventEntity
import spontaniius.data.Repository
import spontaniius.dependency_injection.CreateEventComponent
import spontaniius.dependency_injection.VolleySingleton
import spontaniius.ui.card_collection.CardCollectionFragment
import spontaniius.ui.card_editing.CARD_EDIT_NEW
import spontaniius.ui.card_editing.CardEditingActivity
import spontaniius.ui.create_event.CreateEventFragment
import spontaniius.ui.create_event.MapsFragment
import spontaniius.ui.event_management.EventManagementFragment
import spontaniius.ui.find_event.FindEventFragment
import spontaniius.ui.sign_up.*
import spontaniius.ui.user_menu.UserOptionsActivity
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject


//TODO: rename to MainActivity
class BottomNavigationActivity : AppCompatActivity(),
    CreateEventFragment.OnCreateEventFragmentInteractionListener,
    EventManagementFragment.OnEventManagementFragmentInteractionListener,
    FindEventFragment.OnFindEventFragmentInteractionListener,
    CardCollectionFragment.OnCardCollectionFragmentInteractionListener,
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
    lateinit var previousFragment: Fragment
    lateinit var createEventFragment: CreateEventFragment
    lateinit var promotionFragment: Fragment
    lateinit var cardCollectionFragment: Fragment
    lateinit var eventManagementFragment: EventManagementFragment
    lateinit var eventJoinFragment: EventManagementFragment

    lateinit var findEventFragment: FindEventFragment

    lateinit var optionsMenu: ImageView
    lateinit var actionBarView:View
    lateinit var appContext: Context

    lateinit var iconSelectButton: ImageButton
    lateinit var currentEvent: JSONObject
    var meetupOwner = false
    var eventid = 0
    var eventEnds = ""
    lateinit var userDetails: JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)
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

                    return when (item.getItemId()) {
                        R.id.user_details -> {
                            val intentUserDetails = Intent(appContext,
                                UserOptionsActivity::class.java).apply {

                            }
                            startActivity(intentUserDetails)
                            return true
                        }
                        R.id.edit_card -> {

                            var CurrUserAttributes = getCurrentUserAttributes()
                            val intentUserDetails =
                                Intent(appContext, CardEditingActivity::class.java).apply {
                                    putExtra(USER_NAME, CurrUserAttributes.get("name").toString())
                                    putExtra(PHONE_NUMBER,
                                        CurrUserAttributes.get("phone_number").toString())
                                    putExtra(USER_ID, Amplify.Auth.currentUser.userId)
                                    putExtra(CARD_EDIT_NEW, false)
                                }

                            startActivity(intentUserDetails)
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
                        }
                        else -> false
                    }
                }
            })
            popup.show()
        }





        bottomNavigation = findViewById(R.id.bottom_navigation)
        fragment_container = findViewById(R.id.fragment_container)
        createEventFragment = CreateEventFragment.newInstance()
        findEventFragment = FindEventFragment.newInstance()
        cardCollectionFragment = CardCollectionFragment.newInstance()

        //TODO: when Cord is done with his promotion fragment, create that here
//        TODO: Then, have this class (BottomNavigationActivity) implement all the methods that the fragment calls (probably want to define an interface for that)
//            Note: the above can be accomplished with a lil copy-paste
//        TODO: mark old classes as deprecated (see CreateEventActivity.kt for an example of how to do that)
//        After the refactoring has been stable for a few commits, feel free to remove the old classes entirely (git has records of them, if they are really needed)
        promotionFragment = FindPromotionsFragment.newInstance()



        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, promotionFragment, null).hide(promotionFragment).commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, createEventFragment, null).hide(createEventFragment)
            .commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, findEventFragment, null).commit()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, cardCollectionFragment, null).hide(cardCollectionFragment).commit()


        currentFragment = findEventFragment
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.find_event -> {
                    supportFragmentManager.beginTransaction().hide(currentFragment)
                        .show(findEventFragment).commit()
                    previousFragment = currentFragment
                    currentFragment = findEventFragment
                    true
                }
                R.id.create_event -> {
                    if (meetupOwner) {
                        supportFragmentManager.beginTransaction().hide(currentFragment)
                            .show(eventManagementFragment).commit()
                        previousFragment = currentFragment
                        currentFragment = eventManagementFragment
                        true
                    } else {
                        supportFragmentManager.beginTransaction().hide(currentFragment)
                            .show(createEventFragment).commit()
                        previousFragment = currentFragment
                        currentFragment = createEventFragment
                        true
                    }


                }
                R.id.promotions -> {
                    supportFragmentManager.beginTransaction().hide(currentFragment)
                        .show(promotionFragment).commit()
                    previousFragment = currentFragment
                    currentFragment = promotionFragment
                    true
                }

                R.id.card_collection -> {
                    supportFragmentManager.beginTransaction().hide(currentFragment)
                        .show(cardCollectionFragment).commit()
                    previousFragment = currentFragment
                    currentFragment = cardCollectionFragment
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    override fun onBackPressed() {
       // currently back button is prone to causing kerfuffles, so disabled for now

        /*
        if(this::previousFragment.isInitialized && currentFragment!=previousFragment)
        supportFragmentManager.beginTransaction().hide(currentFragment)
            .show(previousFragment).commit()
        previousFragment = currentFragment
        return

        */
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
                Response.Listener<JSONObject> { response ->
                    val JSONResponse = JSONObject(response.toString())
                    eventid = JSONResponse.get("eventid") as Int
                    meetupOwner = true
                    loadingProgress.visibility=GONE

                    eventManagementFragment =
                        EventManagementFragment.newInstance(eventid.toString(),
                            meetupOwner)
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, eventManagementFragment, null).hide(
                            currentFragment).commitNow()
                    currentFragment = eventManagementFragment
                },
                Response.ErrorListener { error ->
                    loadingProgress.visibility=GONE
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
                supportFragmentManager.beginTransaction().hide(currentFragment)
                    .show(createEventFragment).remove(eventManagementFragment).commitNow()
                currentFragment = createEventFragment
                meetupOwner = false
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
                "yyyy-[M][MM]-[d][dd]['T'][ ][HH][H]:[m][mm]:ssz"
            )
        )

        var eventEndsTime = newEndTime.plusMinutes(15)


        eventEnds = eventEndsTime.format(DateTimeFormatter.ofPattern(
            "yyyy-M-dd HH:mm:ssz"
        )).toString()



        val url ="https://217wfuhnk6.execute-api.us-west-2.amazonaws.com/default/createSpontaniiusEvent?eventid=$eventid&newEndTime="+eventEnds;
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



    override fun openEventChatroom(chatEventid: String, event: JSONObject){
        if (eventid.toString() == chatEventid && meetupOwner==true){ // this is a check that you don't already own this event
            supportFragmentManager.beginTransaction().hide(currentFragment)
                .show(eventManagementFragment).commit()
            previousFragment = currentFragment
            currentFragment = eventManagementFragment
        }else{
            meetupOwner = false
            currentEvent = event
            eventManagementFragment = EventManagementFragment.newInstance(chatEventid, meetupOwner)
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, eventManagementFragment, null).hide(currentFragment).commitNow()
            currentFragment = eventManagementFragment
        }
    }

    override fun switchToCreate() {
        bottomNavigation.selectedItemId = R.id.create_event
    }

    override fun exitEvent(){
        supportFragmentManager.beginTransaction().hide(currentFragment)
            .show(findEventFragment).remove(eventManagementFragment).commitNow()
        currentFragment = findEventFragment
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

    override fun getCurrentUserAttributes():JSONObject{
        // We check to see if user details have already been initialized or not, if not then prepares to
        // collect details
        if(!this::userDetails.isInitialized) {
            var userAttributes: List<AuthUserAttribute?>? = null
            val cardExchangeDetails = JSONObject()
            Amplify.Auth.fetchUserAttributes(
                { attributes: List<AuthUserAttribute?> ->
                    Log.e(
                        "AuthDemo",
                        attributes.toString()
                    )
                    userAttributes = attributes
                    //     initializeUserData(nameEditText, phoneNumberTextView, genderRadioGroup, userAttributes)
                }
            ) { error: AuthException? ->
                Log.e(
                    "AuthDemo",
                    "Failed to fetch user attributes.",
                    error
                )
            }

            var startTime = Calendar.getInstance().timeInMillis

            while (userAttributes == null) {
                // waiting for attributes before moving forward
                if ((Calendar.getInstance().timeInMillis - startTime > 5000)) {
                    Toast.makeText(
                        this,
                        "We weren't able to get your user data, please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                    break
                }

            }

            val currentUserAttributeObject = JSONObject()
            try {
                currentUserAttributeObject.put("userid", Amplify.Auth.currentUser.userId)
                for (attribute in userAttributes!!) {
                    var arributeName = attribute?.key?.keyString
                    if (arributeName == "custom:cardid") {
                        currentUserAttributeObject.put("cardid", attribute?.value)
                    }
                    if (arributeName == "phone_number") {
                        currentUserAttributeObject.put("phone_number", attribute?.value)
                    }
                    if (arributeName == "gender") {
                        currentUserAttributeObject.put("gender", attribute?.value)
                    }
                    if (arributeName == "name") {
                        currentUserAttributeObject.put("name", attribute?.value)
                    }
                }

            } catch (e: JSONException) {
                // handle exception
            }
            userDetails = currentUserAttributeObject
        }

        return userDetails
       }


    override  fun refreshEventDetails():JSONObject {

        return JSONObject()

        }

    override fun updateCardCollectionFragment() {
        // First we remove the old fragment.
        supportFragmentManager.beginTransaction()
                .remove(cardCollectionFragment).commitNow()


        // Then we create a new one.
        cardCollectionFragment = CardCollectionFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, cardCollectionFragment, null).hide(cardCollectionFragment).commit()
    }
}


