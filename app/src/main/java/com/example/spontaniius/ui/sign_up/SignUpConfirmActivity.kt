package com.example.spontaniius.ui.sign_up

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.example.spontaniius.R
import com.example.spontaniius.dependency_injection.VolleySingleton
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList




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




        confirm.setOnClickListener{
            Amplify.Auth.confirmSignUp(
                name,
                confirmationCodeEditText.text.toString(),

                { result ->
                    Log.i("AuthQuickstart", if (result.isSignUpComplete) "Confirm signUp succeeded" else "Confirm sign up not complete")
                    val intent = Intent(this, SignUpActivity2::class.java).apply {
                        putExtra(USER_NAME, name)
                        putExtra(PHONE_NUMBER, phone)
                        putExtra(USER_ID, userid)

                    }

                    startActivity(intent)
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

