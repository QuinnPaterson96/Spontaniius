package com.example.spontaniius.ui.sign_up

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.spontaniius.R

class SignUpActivity :
    AppCompatActivity(),
    SignUpFragment.OnSignUpFragmentInteractionListener
{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportFragmentManager.beginTransaction().add(
            R.id.sign_up_container,
            SignUpFragment.newInstance(),
            "SIGN_UP_FRAGMENT_TAG").commit()
    }
}
