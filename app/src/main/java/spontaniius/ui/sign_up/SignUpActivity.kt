package spontaniius.ui.sign_up

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
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
import org.json.JSONException
import org.json.JSONObject
import spontaniius.R
import spontaniius.dependency_injection.VolleySingleton
import spontaniius.ui.BottomNavigationActivity
import spontaniius.ui.login.LoginActivity
import java.util.*
import kotlin.collections.ArrayList


const val USER_NAME = "spontaniius.ui.sign_up.MESSAGE"
const val PHONE_NUMBER = "spontaniius.ui.sign_up.MESSAGE2"
const val USER_ID = "spontaniius.ui.sign_up.MESSAGE3"



class SignUpActivity : AppCompatActivity(), SignUpFragment.OnSignUpFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            try {
                val config = AmplifyConfiguration.builder(applicationContext)
                    .devMenuEnabled(false)
                    .build()
                Amplify.configure(config, applicationContext)
                Log.i("MyAmplifyApp", "Initialized Amplify")
            } catch (error: AmplifyException) {
                Log.e("MyAmplifyApp", "Could not initialize Amplify", error)
            }

        }catch (error: Exception){

        }

/*
        try{
            val permissions = arrayOf<String>()

            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

            permissionsToRequest = permissionsToRequest(permissions);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(
                        permissionsToRequest.toArray(new String [permissionsToRequest.size()]),
                        ALL_PERMISSIONS_RESULT
                    );
                }
            }

        }catch (error: Exception){

        }
*/

        Amplify.Auth.fetchAuthSession(
            { result ->
                val intent2 = Intent(this, BottomNavigationActivity::class.java).apply {

                }

                if (result.isSignedIn == true) {
                    startActivity(intent2)
                }

                Log.i("AmplifyQuickstart", result.toString())

            },
            { error -> Log.e("AmplifyQuickstart", error.toString()) }
        )
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
            ccp =  findViewById(R.id.ccp);

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


            var username = put_name+rnd.nextInt(1000).toString()
            Amplify.Auth.signUp(
                username,
                put_password,
                AuthSignUpOptions.builder().userAttributes(
                    attributeList
                ).build(),
                { result ->
                    Log.i("AuthQuickStart", "Result: $result")
                    val intent = Intent(this, SignUpConfirmActivity::class.java).apply {
                        putExtra(USER_NAME, username)
                        putExtra(PHONE_NUMBER, ccp.selectedCountryCodeWithPlus + put_phone)
                        putExtra(USER_ID, result.user?.userId)
                        putExtra("Password", put_password)

                    }

                    startActivity(intent)

                },
                { error -> Log.e("AuthQuickStart", "Sign up failed", error) }
            )
/*
            val createUserRequest = JsonObjectRequest(
                Request.Method.PUT, url, userObject,
                Response.Listener { response ->
                    val userid = JSONObject(response.toString()).getInt("userid")
                    val intent = Intent(this, SignUpActivity2::class.java).apply {
                        putExtra(USER_NAME, put_name)
                        putExtra(PHONE_NUMBER, put_phone)
                        putExtra(USER_ID, userid)

                    }

                    startActivity(intent)
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, "err" + error.toString(), Toast.LENGTH_LONG).show()
                }
            )
            queue.add(createUserRequest)
            */

        }

        login.setOnClickListener{
            val intent2 = Intent(this, LoginActivity::class.java).apply {

            }

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

