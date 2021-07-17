package spontaniius.ui.user_menu

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobileconnectors.cognitoauth.Auth
import com.amplifyframework.auth.AuthCategory
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.core.Amplify
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.delay
import spontaniius.R
import java.util.*


class UserOptionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_options)
        val phoneNumberTextView = findViewById<EditText>(R.id.phone_number_edittext)
        val nameEditText = findViewById<EditText>(R.id.name_edittext)
        val genderRadioGroup = findViewById<RadioGroup>(R.id.gender_selection_radioGroup)

        val saveButton = this.findViewById<FloatingActionButton>(R.id.save_user_details)
        val cancelButton = this.findViewById<FloatingActionButton>(R.id.cancel_user_details)
        var details = false
        var userAttributes: List<AuthUserAttribute?>? = null

        saveButton.setOnClickListener {
            val phoneNumber = phoneNumberTextView.text
            val name = nameEditText.text
            val selectedId = genderRadioGroup.getCheckedRadioButtonId()
            var radioButton:RadioButton = this.findViewById(selectedId);




            var attributes:List<AuthUserAttribute?> = listOf(
                AuthUserAttribute(AuthUserAttributeKey.phoneNumber(), phoneNumber.toString()),
                AuthUserAttribute(AuthUserAttributeKey.name(), name.toString()),
                AuthUserAttribute(AuthUserAttributeKey.gender(), radioButton.text.toString()))
            Amplify.Auth.updateUserAttributes(attributes,
                {
                    Log.i("AuthDemo", "Updated user attribute = $it")
                },
                { Log.e("AuthDemo", "Failed to update user attribute.", it) })

            Toast.makeText(
                this,
                "Successfully Updated User Details",
                Toast.LENGTH_LONG
            ).show()


            //TODO: save data here
        }


        cancelButton.setOnClickListener {
            finish()
        }

        var currUser = Amplify.Auth.currentUser

        var str_result= Amplify.Auth.fetchUserAttributes(
            { attributes: List<AuthUserAttribute?> ->
                Log.e(
                    "AuthDemo",
                    attributes.toString()
                )
               userAttributes = attributes
           //     initializeUserData(nameEditText, phoneNumberTextView, genderRadioGroup, userAttributes)
            }
        ) { error: AuthException? ->
            Log.e(
                "AuthDemo",
                "Failed to fetch user attributes.",
                error
            )
        }

        var startTime = Calendar.getInstance().timeInMillis
        while (userAttributes==null){
            // waiting for attributes before moving forward
            if((Calendar.getInstance().timeInMillis - startTime > 5000)){
                Toast.makeText(
                    this,
                    "We weren't able to get your user data, please try again later",
                    Toast.LENGTH_LONG
                ).show()
                break
            }
        }


        initializeUserData(nameEditText, phoneNumberTextView, genderRadioGroup, userAttributes)

    }

    fun initializeUserData(
        nameEditText: EditText,
        phoneNumberEditText: EditText,
        genderRadioGroup: RadioGroup,
        attributes:List<AuthUserAttribute?>?
    ) {

        for(attribute in attributes!!){
            var arributeName = attribute?.key?.keyString
            if(arributeName=="phone_number"){
                phoneNumberEditText.setText(attribute?.value)
            }
            if(arributeName=="gender"){
                if(attribute?.value == "Male"){
                    genderRadioGroup.check(genderRadioGroup.getChildAt(0).id)
                }
                if(attribute?.value == "Female"){
                    genderRadioGroup.check(genderRadioGroup.getChildAt(1).id)
                }
                if(attribute?.value == "Non-Binary"){
                    genderRadioGroup.check(genderRadioGroup.getChildAt(2).id)
                }
                if(attribute?.value == "Other/Prefer not to say"){
                    genderRadioGroup.check(genderRadioGroup.getChildAt(3).id)
                }
            }
            if(arributeName=="name"){
                nameEditText.setText(attribute?.value)
            }

        }

    }

}