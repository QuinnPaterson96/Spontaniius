package spontaniius.ui.landing_screen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.spontaniius.R


class LandingScreenActivity : AppCompatActivity() {

    lateinit var loginButton:Button
    lateinit var emailSignupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_screen)
        loginButton = findViewById(R.id.login_button_landing)
        emailSignupButton = findViewById(R.id.email_signup)

        loginButton.setOnClickListener {

/*            val intent2 = Intent(this, LoginActivity::class.java).apply {}
            startActivity(intent2)
            #TODO Fix this*/
        }
        emailSignupButton.setOnClickListener {
            //val intent3 = Intent(this, SignUpActivity::class.java).apply {}
           // startActivity(intent3)
        }

    }
}