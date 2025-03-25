package spontaniius

import android.app.Application
import android.util.Log
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.google.firebase.messaging.FirebaseMessaging
import com.spontaniius.R
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpontaniiusApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)


            var locationAPIKey = getString(R.string.google_api_key)
            Places.initialize(applicationContext, locationAPIKey)

            FirebaseMessaging.getInstance().subscribeToTopic("spontaniius_notifications")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FCM_Debug", "Successfully subscribed to topic: spontaniius_notifications")
                    } else {
                        Log.e("FCM_Debug", "Subscription failed", task.exception)
                    }
                }

        }catch (error: Exception){
            Log.e("MyAmplifyApp", error.stackTraceToString())
        }
    }
}


