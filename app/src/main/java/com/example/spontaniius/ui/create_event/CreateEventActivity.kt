package com.example.spontaniius.ui.create_event

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.example.spontaniius.R

class CreateEventActivity :
    AppCompatActivity(),
    CreateEventFragment.OnCreateEventFragmentInteractionListener {

    private val pickRequestCode = 1
    private val createEventFragmentTag = "CREATE EVENT TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)
        setupActionBar()
        supportFragmentManager.beginTransaction().add(
            R.id.create_event_container,
            CreateEventFragment.newInstance(),
            createEventFragmentTag
        ).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickRequestCode && resultCode == Activity.RESULT_OK){
            val currentFragment = supportFragmentManager.findFragmentByTag(createEventFragmentTag)
            if (currentFragment is CreateEventFragment){
                val imageUri = data?.data
                if (imageUri != null) {

//                    Deprecated method replacement is not available until API 29, current min is below that
                val imageBitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    currentFragment.updateIcon(imageBitmap)
                }
            }
        }
    }

    private fun setupActionBar(){
        actionBar?.title = getString(R.string.create_event_title)
    }

    override fun selectEventIcon(): Any? {
        val chooseIconIntent = Intent()
        chooseIconIntent.type = "image/*"
        chooseIconIntent.action = Intent.ACTION_PICK

        val takePhotoIconIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val chooseActionTitle = getString(R.string.prompt_choose_icon_input)
        val choiceIntent = Intent.createChooser(chooseIconIntent, chooseActionTitle)
        val intentExtrasArray: Array<Intent> = Array(1){takePhotoIconIntent}

        choiceIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            intentExtrasArray
        )

        startActivityForResult(choiceIntent, pickRequestCode)
        return -1
    }

    override fun selectLocation(): Any? {
        val currentFragment = supportFragmentManager.findFragmentByTag(createEventFragmentTag)
        if (currentFragment != null) {
            supportFragmentManager.beginTransaction().hide(currentFragment)
                .add(R.id.create_event_container, MapsFragment()).commit()
        }
        return -1
    }

    override fun createEvent(
        title: String,
        description: String,
        startTime: Long,
        endTime: Long,
        location: Any?,
        gender: Any?,
        invitation: Any?
    ) {

//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
