package spontaniius

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SpontaniiusApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            try {
                val config = AmplifyConfiguration.builder(applicationContext)
                    .devMenuEnabled(false)
                    .build()
                Amplify.configure(config, applicationContext)



                Log.i("MyAmplifyApp", "Initialized Amplify")
            } catch (error: AmplifyException) {
                Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
            }

        }catch (error: Exception){
            Log.e("MyAmplifyApp", error.stackTraceToString())
        }
    }
}


