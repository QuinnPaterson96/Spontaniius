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
import com.android.volley.Response
import com.example.spontaniius.R
import com.example.spontaniius.dependency_injection.VolleySingleton
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

const val USER_NAME = "com.example.spontaniius.ui.sign_up.MESSAGE"
const val PHONE_NUMBER = "com.example.spontaniius.ui.sign_up.MESSAGE2"
const val USER_ID = "com.example.spontaniius.ui.sign_up.MESSAGE3"



class SignUpActivity : AppCompatActivity(), SignUpFragment.OnSignUpFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Amplify.addPlugin(AWSCognitoAuthPlugin())
        try {
            Amplify.configure(applicationContext)
            Log.i("MyAmplifyApp", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
        }
        /*
        Amplify.Auth.fetchAuthSession(
            { result -> Log.i("AmplifyQuickstart", result.toString()) },
            { error -> Log.e("AmplifyQuickstart", error.toString()) }
        )
        */
        setContentView(R.layout.activity_sign_up)

        supportFragmentManager.beginTransaction().add(
            R.id.sign_up_container,
            SignUpFragment.newInstance(),
            "SIGNUP_FRAGMENT_TAG"
        ).commit()


        //Variables for gender drop down
        lateinit var option: Spinner


        val done = findViewById<Button>(R.id.done_button)


        // Get a RequestQueue
        val queue = VolleySingleton.getInstance(this.applicationContext).requestQueue

        // ...

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        val url =
            "https://217wfuhnk6.execute-api.us-west-2.amazonaws.com/default/postgresCreateUser"

        done.setOnClickListener {

            val user = findViewById<EditText>(R.id.userName)
            val enter_phone = findViewById<EditText>(R.id.phoneNumber)
            val enter_pass = findViewById<EditText>(R.id.password)
            val enter_gender = findViewById<Spinner>(R.id.gender)
            val put_name = user.text.toString()
            val put_phone = enter_phone.text.toString()
            val put_password = enter_pass.text.toString()
            val put_gender = enter_gender.selectedItem.toString()

            val userObject = JSONObject()
            try {

                userObject.put("password", put_password)
                userObject.put("name", put_name)
                userObject.put("gender", put_gender)
                userObject.put("phone", put_phone)

            } catch (e: JSONException) {
                // handle exception
            }

            val rnd = Random()
            rnd.setSeed(System.currentTimeMillis())

            var attributeList: ArrayList<AuthUserAttribute> = ArrayList()
            attributeList.add(AuthUserAttribute(AuthUserAttributeKey.name(), put_name))
            attributeList.add(AuthUserAttribute(AuthUserAttributeKey.gender(), put_gender))
            attributeList.add(AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), "+1"+put_phone))


            var username = put_name+rnd.nextInt(1000).toString()
            Amplify.Auth.signUp(
                username,
                put_password,
                AuthSignUpOptions.builder().userAttributes(
                    attributeList
                ).build(),
                { result ->
                    Log.i("AuthQuickStart", "Result: $result")
                    val intent = Intent(this, SignUpConfirmActivity::class.java).apply {
                        putExtra(USER_NAME, username)
                        putExtra(PHONE_NUMBER, put_phone)
                        putExtra(USER_ID, result.user?.userId)

                    }

                    startActivity(intent)

                },
                { error -> Log.e("AuthQuickStart", "Sign up failed", error) }
            )
/*
            val createUserRequest = JsonObjectRequest(
                Request.Method.PUT, url, userObject,
                Response.Listener { response ->
                    val userid = JSONObject(response.toString()).getInt("userid")
                    val intent = Intent(this, SignUpActivity2::class.java).apply {
                        putExtra(USER_NAME, put_name)
                        putExtra(PHONE_NUMBER, put_phone)
                        putExtra(USER_ID, userid)

                    }

                    startActivity(intent)
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "err" + error.toString(), Toast.LENGTH_LONG).show()
                }
            )
            queue.add(createUserRequest)
            */

        }


        //sets varables to the item in the layout
        option = findViewById<Spinner>(R.id.gender)

        val options = arrayOf("Male", "Female", "Other")


        option.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)



            option.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                //no input
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                //selected item recieved
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                }
            }


        }
    }

