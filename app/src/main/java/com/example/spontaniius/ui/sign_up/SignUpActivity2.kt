package com.example.spontaniius.ui.sign_up

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.spontaniius.R
import com.example.spontaniius.R.layout.activity_sign_up2
import com.example.spontaniius.dependency_injection.VolleySingleton
import org.json.JSONException
import org.json.JSONObject


const val GREETING = "com.example.spontaniius.ui.sign_up.MESSAGE3"


class SignUpActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(activity_sign_up2)

        val back = findViewById<Button>(R.id.back_button)
        val hug_b = findViewById<Button>(R.id.hug_button)
        val highfive_b = findViewById<Button>(R.id.highfive_button)
        val handshake_b = findViewById<Button>(R.id.handshake_button)
        val wave_b = findViewById<Button>(R.id.wave_button)
        val done2 = findViewById<Button>(R.id.done_button2)
        val name = intent.getStringExtra(USER_NAME)
        val phone = intent.getStringExtra(PHONE_NUMBER)
        val userid = intent.getStringExtra(USER_ID)


        var greeting : String = ""




        wave_b.setOnClickListener{
            greeting = getString(R.string.wave_string)
        }
        highfive_b.setOnClickListener{
            greeting = getString(R.string.five_string)
        }
        handshake_b.setOnClickListener{
            greeting = getString(R.string.shake_string)
        }
        hug_b.setOnClickListener{
            greeting = getString(R.string.hug_string)
        }


        back.setOnClickListener{
            val intent3 = Intent(this, SignUpActivity::class.java)
            startActivity(intent3)
        }

        done2.setOnClickListener{

            val intent = Intent(this, SignUpActivity3::class.java).apply{
                putExtra(USER_NAME, name)
                putExtra(PHONE_NUMBER, phone)
                putExtra(GREETING, greeting)
                putExtra(USER_ID, userid)
            }


            startActivity(intent)
        }

    }
}
