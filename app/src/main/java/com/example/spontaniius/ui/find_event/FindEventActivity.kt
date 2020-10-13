package com.example.spontaniius.ui.find_event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spontaniius.R
import com.example.spontaniius.SpontaniiusApplication
import com.example.spontaniius.dependency_injection.CreateEventComponent
import com.example.spontaniius.dependency_injection.FindEventComponent
import com.example.spontaniius.ui.create_event.CreateEventFragment
import com.example.spontaniius.ui.create_event.MapsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

private val FindEventFragmentTag = "FIND EVENT TAG"

class FindEventActivity : AppCompatActivity(){
    private lateinit var findEventComponent: FindEventComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findEventComponent =
            (applicationContext as SpontaniiusApplication).applicationComponent.FindEventComponent()
                .create()
        findEventComponent.inject(this)
        setContentView(R.layout.activity_find_event)
        setTitle(R.string.find_event_title)
        supportFragmentManager.beginTransaction().add(
            R.id.find_event_container,
           FindEventFragment.newInstance()
        ).commit()

    }
}
