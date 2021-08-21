package spontaniius.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.amplifyframework.core.Amplify

import spontaniius.R
import spontaniius.ui.BottomNavigationActivity
import spontaniius.ui.sign_up.SignUpActivity
import com.rilixtech.widget.countrycodepicker.CountryCodePicker

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val signup = findViewById<Button>(R.id.signup_button)
        val login = findViewById<Button>(R.id.login_button)
        val loading = findViewById<ProgressBar>(R.id.loading)
        val forgotPasswordButton = findViewById<Button>(R.id.forgot_password_button)
        var ccp: CountryCodePicker
        ccp =  findViewById(R.id.ccp);

        forgotPasswordButton.setOnClickListener {
            val resetIntent = Intent(this, ResetPasswordActivity::class.java).apply {
                putExtra(USERNAME, username.text.toString())
            }
            startActivity(resetIntent)
        }



            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                Amplify.Auth.signIn(
                    ccp.selectedCountryCodeWithPlus +username.text.toString(),
                    password.text.toString(),
                    { result -> Log.i("AuthQuickstart", if (result.isSignInComplete) "Sign in succeeded" else "Sign in not complete")
                        loading.visibility = View.INVISIBLE

                        val intent2 = Intent(this, BottomNavigationActivity::class.java).apply {

                        }
                        startActivity(intent2)
                    },
                    { error -> Log.e("AuthQuickstart", error.toString())
                        loading.visibility = View.INVISIBLE}
                )
            }

            signup.setOnClickListener {
                val intent3 = Intent(this, SignUpActivity::class.java).apply {

                }
                startActivity(intent3)
            }
        }
    }



/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}