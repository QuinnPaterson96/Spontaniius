package com.example.spontaniius.ui.sign_up

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.spontaniius.R
import android.view.View
import androidx.cardview.widget.CardView
import com.example.spontaniius.ui.find_event.FindEventActivity


class SignUpActivity3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up3)

        val spinner: Spinner = findViewById(R.id.color_spinner)
        val back2 = findViewById<Button>(R.id.back_button2)
        val name = intent.getStringExtra(USER_NAME)
        val phone = intent.getStringExtra(PHONE_NUMBER)
        val greet = intent.getStringExtra(GREETING)
        val userid = intent.getStringExtra(USER_ID)
        val greeting_card : CardView = findViewById(R.id.greeting_card)
        val phone_s : String = getString(R.string.phone_string)
        val greet_s : String = getString(R.string.special_string)

        val phone_box : CheckBox = findViewById(R.id.include_phone)
        val greet_box : CheckBox = findViewById(R.id.include_greeting)


        val enter_name = findViewById<TextView>(R.id.enter_name).apply{
            text = name
        }

        val done3 = findViewById<Button>(R.id.done_button3)



        phone_box.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                findViewById<TextView>(R.id.display_phone).apply{
                    text = phone
                }
                findViewById<TextView>(R.id.phone).apply{
                    text = phone_s
                }
            }else{
                findViewById<TextView>(R.id.display_phone).apply{
                    text = ""
                }
                findViewById<TextView>(R.id.phone).apply{
                    text = ""
                }
            }

        }


        greet_box.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

                findViewById<TextView>(R.id.special_greeting).apply{
                    text = greet_s
                }
                findViewById<TextView>(R.id.display_greeting).apply{
                    text = greet
                }
            }else{

                findViewById<TextView>(R.id.special_greeting).apply{
                    text = ""
                }
                findViewById<TextView>(R.id.display_greeting).apply{
                    text = ""
                }
            }

        }

        ArrayAdapter.createFromResource(this, R.array.color_array, android.R.layout.simple_spinner_item).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }


        back2.setOnClickListener{
            val intent4 = Intent(this, SignUpActivity2::class.java)
            startActivity(intent4)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(pos).toString()


                if (selectedItem.equals("Red")) {
                    greeting_card.setCardBackgroundColor(Color.parseColor("#FF8E8E"))

                }else if(selectedItem.equals("Orange")){
                    greeting_card.setCardBackgroundColor(Color.parseColor("#FFB549"))

                }else if(selectedItem.equals("Yellow")){
                    greeting_card.setCardBackgroundColor(Color.parseColor("#FFF55C"))

                }else if(selectedItem.equals("Green")){
                    greeting_card.setCardBackgroundColor(Color.parseColor("#8FFF7B"))

                }else if(selectedItem.equals("Blue")){
                    greeting_card.setCardBackgroundColor(Color.parseColor("#7BE1FF"))

                }else if(selectedItem.equals("Purple")){
                    greeting_card.setCardBackgroundColor(Color.parseColor("#BB8BFF"))

                }else if(selectedItem.equals("Pink")){
                    greeting_card.setCardBackgroundColor(Color.parseColor("#FF8BED"))

                }else if(selectedItem.equals("White")){
                    greeting_card.setCardBackgroundColor(Color.WHITE)
                }
            }



            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }

        }

        done3.setOnClickListener{

            val intent = Intent(this, FindEventActivity::class.java).apply{

            }
            startActivity(intent)
        }

    }

}
