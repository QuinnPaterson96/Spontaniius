package com.example.spontaniius.ui.promotions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.spontaniius.R

class PromotionTab : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val PromoTitle = "This is a title"
        val PromoContent = "This is content for a promomotion so that's cool yeetyoatboatmoat beach times kk bye bb"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotion_tab)
        val Title: TextView = findViewById (R.id.Title)
        Title.setText(PromoTitle)
        val Content: TextView = findViewById (R.id.Content)
        Content.setText(PromoContent)
        val button1: Button = findViewById(R.id.BackButton)
        button1.setOnClickListener {
            // Handler code here.
            //val intent = Intent(this, PromotionTab::class.java)
            //startActivity(intent);
            this.finish()
        }
    }
}
