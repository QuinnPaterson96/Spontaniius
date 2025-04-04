package spontaniius.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar


import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import spontaniius.common.AuthViewModel
import spontaniius.common.UserViewModel
import spontaniius.data.local.dao.UserDao
import spontaniius.data.repository.UserRepository
import spontaniius.domain.models.User
import javax.inject.Inject


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

    @Inject
    lateinit var userRepository: UserRepository // ✅ Inject user repository to update the token

    @Inject
    lateinit var userDao: UserDao

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

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FCM_Debug", "Fetching FCM token failed", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result
                Log.d("FCM_Debug", "FCM Token: $token")

                sendTokenToServer(token) // Move token update logic here
            }

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
                            openURL.data = Uri.parse("https://quinnpaterson1996.wixsite.com/spontaniius")
                            startActivity(openURL)
                            return true
                        }

                        R.id.report_an_issue ->{
                            val openURL = Intent(android.content.Intent.ACTION_VIEW)
                            openURL.data = Uri.parse("https://quinnpaterson1996.wixsite.com/spontaniius/contact")
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001 // Request Code
            )
        }
    }
    // This was created to streamline the process of accessing user attributes and to reduce code
    // duplication across program. It fetches the user attributes and returns them as a JSON object
    // If the details have already been fetched it avoids calling AWS Auth again
    private fun sendTokenToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try
            {
                userRepository.updateUserFCMToken(token)
                Log.d("FCM_Debug", "Token successfully sent to server")
            }
            catch (e: Exception) {
                Log.e("FCM_Debug", "Failed to send token to server", e)
            }
        }
    }
}


