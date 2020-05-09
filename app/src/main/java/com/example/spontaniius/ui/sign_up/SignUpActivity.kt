package com.example.spontaniius.ui.sign_up

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.spontaniius.R
import com.example.spontaniius.dependency_injection.VolleySingleton
import org.json.JSONException
import org.json.JSONObject

const val USER_NAME = "com.example.spontaniius.ui.sign_up.MESSAGE"
const val PHONE_NUMBER = "com.example.spontaniius.ui.sign_up.MESSAGE2"
const val USER_ID = "com.example.spontaniius.ui.sign_up.MESSAGE3"



class SignUpActivity : AppCompatActivity(), SignUpFragment.OnSignUpFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportFragmentManager.beginTransaction().add(
            R.id.sign_up_container,
            SignUpFragment.newInstance(),
            "SIGNUP_FRAGMENT_TAG").commit()


        //Variables for gender drop down
        lateinit var option: Spinner


            val done = findViewById<Button>(R.id.done_button)


        // Get a RequestQueue
        val queue = VolleySingleton.getInstance(this.applicationContext).requestQueue

        // ...

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        val url = "https://217wfuhnk6.execute-api.us-west-2.amazonaws.com/default/postgresCreateUser"

        done.setOnClickListener {

                val user = findViewById<EditText>(R.id.userName)
                val enter_phone = findViewById<EditText>(R.id.phoneNumber)
                val enter_pass= findViewById<EditText>(R.id.password)
                val enter_gender= findViewById<Spinner>(R.id.gender)
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

            val createUserRequest = JsonObjectRequest(
                Request.Method.PUT,url, userObject,
                Response.Listener { response ->
                    val  userid =  JSONObject(response.toString()).getInt("userid")
                    val intent = Intent(this, SignUpActivity2::class.java).apply{
                        putExtra(USER_NAME, put_name)
                        putExtra(PHONE_NUMBER, put_phone)
                        putExtra(USER_ID, userid)

                    }

                    startActivity(intent)
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this,"err"+error.toString(),Toast.LENGTH_LONG).show()
                }
            )
            queue.add(createUserRequest)
        }



            //sets varables to the item in the layout
            option = findViewById(R.id.gender) as Spinner

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

