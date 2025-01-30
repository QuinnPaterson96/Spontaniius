package spontaniius.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import spontaniius.ui.landing_screen.LandingScreenActivity
import spontaniius.ui.sign_up.SignUpActivity


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Amplify.Auth.fetchAuthSession(
            { result ->
                val intent2 = Intent(this, BottomNavigationActivity::class.java).apply {
                    finish()
                }

                if (result.isSignedIn == true) {
                    startActivity(intent2)
                }else{
                    startActivity(Intent(this@SplashScreenActivity, LandingScreenActivity::class.java))
                    finish()// Go to landing screen if not logged in
                }

                Log.i("AmplifyQuickstart", result.toString())

            },
            { error ->
                Log.e("AmplifyQuickstart", error.toString())
                startActivity(Intent(this@SplashScreenActivity, LandingScreenActivity::class.java)) // Go to landing screen if issue checking logged in.
                finish()
            }
        )

    }
}