package spontaniius.ui.sign_up

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.spontaniius.R
import spontaniius.ui.card_editing.CardEditingActivity
import spontaniius.ui.login.USERNAME


class SignUpConfirmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_signup)

        val back = findViewById<Button>(R.id.back_button)
        val confirm = findViewById<Button>(R.id.confirm_button)
        val confirmationCodeEditText = findViewById<EditText>(R.id.confirmation_code)
        val username = intent.getStringExtra(USERNAME)
        val name = intent.getStringExtra(USER_NAME)

        val phone = intent.getStringExtra(PHONE_NUMBER)
        val userid = intent.getStringExtra(USER_ID)
        val password = intent.getStringExtra("Password")
        val errorText = findViewById<TextView>(R.id.error_text)
        var eventLoad = findViewById<ProgressBar>(R.id.loading)




        confirm.setOnClickListener{
            errorText.text = ""
            eventLoad.visibility= VISIBLE
            Amplify.Auth.confirmSignUp(
                username!!,
                confirmationCodeEditText.text.toString(),

                { result ->
                    Log.i("AuthQuickstart", if (result.isSignUpComplete) "Confirm signUp succeeded" else "Confirm sign up not complete")
                    Amplify.Auth.signIn(
                        username,
                        password,
                        { result -> Log.i("AuthQuickstart", if (result.isSignedIn) "Sign in succeeded" else "Sign in not complete")
                            val intent = Intent(this, CardEditingActivity::class.java).apply {
                                putExtra(USER_NAME, name)
                                putExtra(PHONE_NUMBER, phone)
                                putExtra(USER_ID, userid)

                            }

                            startActivity(intent)},
                        { error -> Log.e("AuthQuickstart", error.toString())
                            eventLoad.visibility= GONE
                        }
                    )
                },
                { error -> Log.e("AuthQuickstart", error.toString())
                    errorText.text = "wrong authentication code"
                    eventLoad.visibility= GONE
                }
            )
        }


        back.setOnClickListener{
            val intent3 = Intent(this, SignUpActivity::class.java)
            startActivity(intent3)
        }
    }
}

