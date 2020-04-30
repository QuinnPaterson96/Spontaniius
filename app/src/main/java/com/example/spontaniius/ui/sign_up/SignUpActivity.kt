package com.example.spontaniius.ui.sign_up

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.spontaniius.R

const val USER_NAME = "com.example.spontaniius.ui.sign_up.MESSAGE"
const val PHONE_NUMBER = "com.example.spontaniius.ui.sign_up.MESSAGE2"



class SignUpActivity : AppCompatActivity(), SignUpFragment.OnSignUpFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportFragmentManager.beginTransaction().add(
            R.id.signup_container, 
            SignUpFragment.newInstance(),
            "SIGNUP_FRAGMENT_TAG").commit()


        //Variables for gender drop down
        lateinit var option: Spinner


            val done = findViewById<Button>(R.id.done_button)


        done.setOnClickListener {

                val user = findViewById<EditText>(R.id.userName)
                val enter_phone = findViewById<EditText>(R.id.phoneNumber)
                val put_name = user.text.toString()
                val put_phone = enter_phone.text.toString()
                val intent = Intent(this, SignUpActivity2::class.java).apply{
                putExtra(USER_NAME, put_name)
                    putExtra(PHONE_NUMBER, put_phone)
            }
            startActivity(intent)
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

