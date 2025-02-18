package spontaniius

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpontaniiusApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        try {
            FirebaseApp.initializeApp(this)

        }catch (error: Exception){
            Log.e("MyAmplifyApp", error.stackTraceToString())
        }
    }
}


