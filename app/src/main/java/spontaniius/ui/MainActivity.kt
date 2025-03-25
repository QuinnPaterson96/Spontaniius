package spontaniius.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar


import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.common.AuthViewModel
import spontaniius.common.UserViewModel
import spontaniius.domain.models.User


@AndroidEntryPoint
class MainActivity : AppCompatActivity(){
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
    var userDetails: User? = null

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        navController = navHostFragment?.navController ?: throw IllegalStateException("NavController not found")

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setupWithNavController(navController!!)


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
                            navController!!.navigate(R.id.userOptionsFragment)
                            return true
                        }
                        R.id.edit_card -> {
                            val bundle = bundleOf(
                                "name" to userDetails!!.name,
                                "newUser" to false
                            )

                            navController!!.navigate(R.id.cardEditingFragment, bundle)
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
                            authViewModel.checkAuthState()
                            return true
                        }
                        else -> false
                    }
                }
            })
            popup.show()
        }


        navController!!.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.signupFragment, R.id.phoneLoginFragment, R.id.OTPVerificationFragment -> {
                    bottomNavigation.visibility = View.GONE
                    optionsMenu.visibility = View.GONE
                }
                else -> {
                    bottomNavigation.visibility = View.VISIBLE
                    optionsMenu.visibility = View.VISIBLE
                }
            }
        }

        setupObservers()

        authViewModel.checkAuthState()
    }

    fun setupObservers(){
        // User authentication handling
        userViewModel.userAttributes.observe(this) { attributes ->
            if (attributes!=null){
                userDetails = attributes
            }else{
                // Indicates user didn't finish signup process
                authViewModel.signOut()
                navController!!.navigate(R.id.loginFragment)
            }
        }

        authViewModel.externalId.observe(this) { externalId ->
            if (externalId != null) {
                navController!!.navigate(R.id.findEventFragment)
                userViewModel.refreshUserAttributes(externalId)
            } else {
                navController!!.navigate(R.id.loginFragment)
            }
        }
    }


    override fun onBackPressed() {
        navController?.popBackStack()
    }


    // This was created to streamline the process of accessing user attributes and to reduce code
    // duplication across program. It fetches the user attributes and returns them as a JSON object
    // If the details have already been fetched it avoids calling AWS Auth again

}


