package spontaniius.ui.landing_screen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.smarteist.autoimageslider.SliderView
import spontaniius.R
import spontaniius.ui.login.LoginActivity
import spontaniius.ui.sign_up.SignUpActivity


class LandingScreenActivity : AppCompatActivity() {

    lateinit var loginButton:Button
    lateinit var emailSignupButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_screen)


        // we are creating array list for storing our image urls.

        // we are creating array list for storing our image urls.
        val sliderDataArrayList: ArrayList<SliderData> = ArrayList()

        // initializing the slider view.

        // initializing the slider view.
        val sliderView = findViewById<SliderView>(R.id.slider)

        // adding the urls inside array list

        // adding the urls inside array list
        sliderDataArrayList.add(SliderData(R.drawable.onboarding1))
        sliderDataArrayList.add(SliderData(R.drawable.onboarding2))
        sliderDataArrayList.add(SliderData(R.drawable.onboarding3))

        // passing this array list inside our adapter class.

        // passing this array list inside our adapter class.
        val adapter = SliderAdapter(this, sliderDataArrayList)

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.

        // below method is used to set auto cycle direction in left to
        // right direction you can change according to requirement.
        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR

        // below method is used to
        // setadapter to sliderview.

        // below method is used to
        // setadapter to sliderview.
        sliderView.setSliderAdapter(adapter)

        // below method is use to set
        // scroll time in seconds.

        // below method is use to set
        // scroll time in seconds.
        sliderView.scrollTimeInSec = 3

        // to set it scrollable automatically
        // we use below method.

        // to set it scrollable automatically
        // we use below method.
        sliderView.isAutoCycle = true

        // to start autocycle below method is used.

        // to start autocycle below method is used.
        sliderView.startAutoCycle()

        loginButton = findViewById(R.id.login_button_landing)
        emailSignupButton = findViewById(R.id.email_signup)

        loginButton.setOnClickListener {
            val intent2 = Intent(this, LoginActivity::class.java).apply {}
            startActivity(intent2)
        }
        emailSignupButton.setOnClickListener {
            val intent3 = Intent(this, SignUpActivity::class.java).apply {}
            startActivity(intent3)
        }

    }
}