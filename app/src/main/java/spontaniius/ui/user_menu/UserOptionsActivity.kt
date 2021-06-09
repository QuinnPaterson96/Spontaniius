package spontaniius.ui.user_menu

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import spontaniius.R

class UserOptionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_options)
        val phoneNumberTextView = findViewById<EditText>(R.id.phone_number_edittext)
        val nameEditText = findViewById<EditText>(R.id.name_edittext)
        val genderRadioGroup = findViewById<RadioGroup>(R.id.gender_selection_radioGroup)
        initializeUserData(nameEditText, phoneNumberTextView, genderRadioGroup)
        val saveButton = this.findViewById<FloatingActionButton>(R.id.save_user_details)
        val cancelButton = this.findViewById<FloatingActionButton>(R.id.cancel_user_details)
        saveButton.setOnClickListener {
            val phoneNumber = phoneNumberTextView.text
            val name = nameEditText.text
            val gender = genderRadioGroup.isSelected
            //TODO: save data here
        }
        cancelButton.setOnClickListener {
            finish()
        }
    }

    fun initializeUserData(
        nameEditText: EditText,
        phoneNumberEditText: EditText,
        genderRadioGroup: RadioGroup
    ) {
        //TODO: get data and set it to display here
        nameEditText.setText("previously initialized name")
        phoneNumberEditText.setText("123-456-7890")
        genderRadioGroup.check(0)
    }
}