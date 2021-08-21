package spontaniius.ui.sign_up

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.AmplifyConfiguration
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONException
import org.json.JSONObject
import spontaniius.R
import spontaniius.dependency_injection.VolleySingleton
import spontaniius.ui.BottomNavigationActivity
import spontaniius.ui.login.LoginActivity
import spontaniius.ui.login.USERNAME
import java.util.*
import kotlin.collections.ArrayList


const val USER_NAME = "spontaniius.ui.sign_up.MESSAGE"
const val PHONE_NUMBER = "spontaniius.ui.sign_up.MESSAGE2"
const val USER_ID = "spontaniius.ui.sign_up.MESSAGE3"



class SignUpActivity : AppCompatActivity(), SignUpFragment.OnSignUpFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportFragmentManager.beginTransaction().add(
            R.id.sign_up_container,
            SignUpFragment.newInstance(),
            "SIGNUP_FRAGMENT_TAG"
        ).commit()


        //Variables for gender drop down
        lateinit var option: Spinner


        val done = findViewById<Button>(R.id.done_button)
        val login = findViewById<Button>(R.id.login_button)


        // Get a RequestQueue
        val queue = VolleySingleton.getInstance(this.applicationContext).requestQueue

        // ...

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        val url =
            "https://217wfuhnk6.execute-api.us-west-2.amazonaws.com/default/postgresCreateUser"

        done.setOnClickListener {

            val user = findViewById<EditText>(R.id.userName)
            val enter_phone = findViewById<EditText>(R.id.phoneNumber)
            val enter_pass = findViewById<EditText>(R.id.password)
            val enter_gender = findViewById<Spinner>(R.id.gender)
            val put_name = user.text.toString()
            val put_phone = enter_phone.text.toString()
            val put_password = enter_pass.text.toString()
            val put_gender = enter_gender.selectedItem.toString()
            var ccp: CountryCodePicker
            var eventLoad = findViewById<ProgressBar>(R.id.loading)

            ccp =  findViewById(R.id.ccp);
            val errorText = findViewById<TextView>(R.id.error_text)

            val userObject = JSONObject()
            try {

                userObject.put("password", put_password)
                userObject.put("name", put_name)
                userObject.put("gender", put_gender)
                userObject.put("phone", put_phone)

            } catch (e: JSONException) {
                // handle exception
            }

            val rnd = Random()
            rnd.setSeed(System.currentTimeMillis())

            var attributeList: ArrayList<AuthUserAttribute> = ArrayList()
            attributeList.add(AuthUserAttribute(AuthUserAttributeKey.name(), put_name))
            attributeList.add(AuthUserAttribute(AuthUserAttributeKey.gender(), put_gender))
            attributeList.add(
                AuthUserAttribute(
                    AuthUserAttributeKey.phoneNumber(),
                    ccp.selectedCountryCodeWithPlus + put_phone
                )
            )

            errorText.text = ""

            var username = put_name.replace(" ","")+rnd.nextInt(1000).toString()
            eventLoad.visibility=VISIBLE

            Amplify.Auth.signUp(
                username,
                put_password,
                AuthSignUpOptions.builder().userAttributes(
                    attributeList
                ).build(),
                { result ->
                    Log.i("AuthQuickStart", "Result: $result")
                    val intent = Intent(this, SignUpConfirmActivity::class.java).apply {
                        putExtra(USER_NAME, put_name)
                        putExtra(USERNAME, username)
                        putExtra(PHONE_NUMBER, ccp.selectedCountryCodeWithPlus + put_phone)
                        putExtra(USER_ID, result.user?.userId)
                        putExtra("Password", put_password)

                    }

                    startActivity(intent)

                },
                { error -> Log.e("AuthQuickStart", "Sign up failed", error)

                    if(error.message?.toLowerCase()?.contains("password")!!) {
                        errorText.text =
                            ("Your password must be 6 characters long, at least one capital letter, and at least one number")
                    }
                    if(error.toString()?.toLowerCase()?.contains("phone")!!) {
                        errorText.text =
                            ("Phone number isn't valid")
                    }
                    eventLoad.visibility= GONE
                }
            )
        }

        login.setOnClickListener{
            val intent2 = Intent(this, LoginActivity::class.java).apply {}

            startActivity(intent2)
        }

        //sets varables to the item in the layout
        option = findViewById<Spinner>(R.id.gender)

        val options = arrayOf("Male", "Female", "Other")


        option.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)



            option.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                //no input
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                //selected item recieved
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                }
            }


        }
    private fun permissionsToRequest(wantedPermissions: ArrayList<String>): ArrayList<String>? {
        val result: ArrayList<String> = ArrayList()
        for (perm in wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm)
            }
        }
        return result
    }

    private fun hasPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else true
    }
    }

