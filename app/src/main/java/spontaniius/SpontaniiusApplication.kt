package spontaniius

import android.app.Application
import android.util.Log
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
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


        }catch (error: Exception){
            Log.e("MyAmplifyApp", error.stackTraceToString())
        }
    }
}


