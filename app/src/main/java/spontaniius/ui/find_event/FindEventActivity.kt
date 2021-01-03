package spontaniius.ui.find_event

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import spontaniius.R
import spontaniius.SpontaniiusApplication
import spontaniius.dependency_injection.FindEventComponent

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
