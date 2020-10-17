package com.example.spontaniius.ui.promotions

import android.os.Bundle
import android.widget.Button

import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity

import com.example.spontaniius.R
import java.net.IDN

class PromotionTab : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val PromoID = intent.getIntExtra("ID", 0)
        val PromoTitle = "This is a title" //Need to make a database type thing now. Easier though
        val PromoContent = "This is content for a promomotion so that's cool yeetyoatboatmoat beach times kk bye bb"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotion_tab)
        val Title: TextView = findViewById (R.id.Title)
        Title.setText(PromoTitle)
        val Content: TextView = findViewById (R.id.Content)
        Content.setText(PromoContent)
        val IDtest: TextView = findViewById(R.id.IDDebug)
        val IDstring:String = PromoID.toString()
        IDtest.setText(IDstring)
        val button1: Button = findViewById(R.id.BackButton)
        button1.setOnClickListener {
            // Handler code here.
            //val intent = Intent(this, PromotionTab::class.java)
            //startActivity(intent);
            this.finish()
        }
    }
}
