package com.example.spontaniius

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ArrayAdapter
import android.widget.ListView
import android.view.View

class MainActivity : AppCompatActivity() {

//To do: get list view working

    //Default Repo: will have a bunch of infomation. Get data from there after debug builds
    //To make listview work: Get a list adapter going
    //List adapter supplies info for list veiw to make more views

    override fun onCreate(savedInstanceState: Bundle?) { //
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button1: Button = findViewById(R.id.UwU)
        button1.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, PromotionTab::class.java)
            startActivity(intent);
        }
        val myStringArray = arrayOf("Element 1","Element 2","Element 3")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myStringArray)  //This helps set up a layout? https://developer.android.com/guide/topics/ui/declaring-layout.html#FillingTheLayout
        val listView: ListView = findViewById(R.id.list_view)
        listView.adapter = adapter
    }


}

