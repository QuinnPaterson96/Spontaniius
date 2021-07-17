package spontaniius.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.core.Amplify
import com.example.spontaniius.ui.promotions.FindPromotionsFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import spontaniius.R
import spontaniius.SpontaniiusApplication
import spontaniius.data.EventEntity
import spontaniius.data.Repository
import spontaniius.dependency_injection.CreateEventComponent
import spontaniius.ui.create_event.CreateEventFragment
import spontaniius.ui.create_event.MapsFragment
import spontaniius.ui.find_event.FindEventFragment
import spontaniius.ui.login.LoginActivity
import spontaniius.ui.sign_up.SignUpActivity
import spontaniius.ui.user_menu.UserOptionsActivity
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
    lateinit var optionsMenu: ImageView
    lateinit var actionBarView:View
    lateinit var appContext: Context

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
                            val intentUserDetails = Intent(appContext, UserOptionsActivity::class.java).apply {

                            }
                            startActivity(intentUserDetails)
                            return true
                        }
                        R.id.edit_card -> {
                            return true
                        }
                        R.id.about_us -> {
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
                            val intentSignout = Intent(appContext, SignUpActivity::class.java).apply {

                            }
                                startActivity(intentSignout)
                            return true
                        }
                        R.id.delete_account -> {
                            return true
                        }


                        else -> false
                    }
                }
            })
            popup.show()
        }



        createEventComponent =
            (applicationContext as SpontaniiusApplication).applicationComponent.createEventComponent()
                .create()
        createEventComponent.inject(this)

        bottomNavigation = findViewById(R.id.bottom_navigation)
        fragment_container = findViewById(R.id.fragment_container)
        createEventFragment = CreateEventFragment.newInstance()
        findEventFragment = FindEventFragment.newInstance()

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
            Toast.makeText(
                this,
                "Event Created",
                Toast.LENGTH_LONG
            ).show()
        }
    }

}