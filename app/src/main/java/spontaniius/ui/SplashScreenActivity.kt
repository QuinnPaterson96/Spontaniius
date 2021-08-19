package spontaniius.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import spontaniius.MainActivity
import spontaniius.ui.sign_up.SignUpActivity


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this@SplashScreenActivity, SignUpActivity::class.java))
        finish()
    }
}