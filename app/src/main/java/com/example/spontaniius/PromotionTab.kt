package com.example.spontaniius

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class PromotionTab : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotion_tab)
        val button1: Button = findViewById(R.id.BackButton)
        button1.setOnClickListener {
            // Handler code here.
            //val intent = Intent(this, PromotionTab::class.java)
            //startActivity(intent);
            this.finish()
        }
    }
}
