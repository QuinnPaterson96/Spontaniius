package spontaniius.ui.sign_up

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import spontaniius.R
import spontaniius.ui.card_editing.CardEditingActivity


class SignUpConfirmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_signup)

        val back = findViewById<Button>(R.id.back_button)
        val confirm = findViewById<Button>(R.id.confirm_button)
        val confirmationCodeEditText = findViewById<EditText>(R.id.confirmation_code)
        val name = intent.getStringExtra(USER_NAME)
        val phone = intent.getStringExtra(PHONE_NUMBER)
        val userid = intent.getStringExtra(USER_ID)
        val password = intent.getStringExtra("Password")




        confirm.setOnClickListener{
            Amplify.Auth.confirmSignUp(
                name!!,
                confirmationCodeEditText.text.toString(),

                { result ->
                    Log.i("AuthQuickstart", if (result.isSignUpComplete) "Confirm signUp succeeded" else "Confirm sign up not complete")
                    Amplify.Auth.signIn(
                        name,
                        password,
                        { result -> Log.i("AuthQuickstart", if (result.isSignInComplete) "Sign in succeeded" else "Sign in not complete")
                            val intent = Intent(this, CardEditingActivity::class.java).apply {
                                putExtra(USER_NAME, name)
                                putExtra(PHONE_NUMBER, phone)
                                putExtra(USER_ID, userid)

                            }

                            startActivity(intent)},
                        { error -> Log.e("AuthQuickstart", error.toString()) }
                    )
                },
                { error -> Log.e("AuthQuickstart", error.toString()) }
            )
        }


        back.setOnClickListener{
            val intent3 = Intent(this, SignUpActivity::class.java)
            startActivity(intent3)
        }
    }
}

