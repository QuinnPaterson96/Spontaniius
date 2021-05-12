package spontaniius.ui.find_event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import spontaniius.R

private val FindEventFragmentTag = "FIND EVENT TAG"

class FindEventActivity : AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_event)
        setTitle(R.string.find_event_title)
        supportFragmentManager.beginTransaction().add(
            R.id.find_event_container,
           FindEventFragment.newInstance()
        ).commit()

    }
}
