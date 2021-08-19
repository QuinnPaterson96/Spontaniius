package spontaniius.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import spontaniius.MainActivity
import spontaniius.ui.landing_screen.LandingScreenActivity
import spontaniius.ui.sign_up.SignUpActivity


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        }

        Amplify.Auth.fetchAuthSession(
            { result ->
                val intent2 = Intent(this, BottomNavigationActivity::class.java).apply {

                }

                if (result.isSignedIn == true) {
                    startActivity(intent2)
                }

                Log.i("AmplifyQuickstart", result.toString())

            },
            { error -> Log.e("AmplifyQuickstart", error.toString()) }
        )
        startActivity(Intent(this@SplashScreenActivity, LandingScreenActivity::class.java))
        finish()
    }
}