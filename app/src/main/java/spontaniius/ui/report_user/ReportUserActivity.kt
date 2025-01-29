package spontaniius.ui.report_user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.amplifyframework.core.Amplify
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import com.spontaniius.R
import spontaniius.di.VolleySingleton
import spontaniius.ui.BottomNavigationActivity
import spontaniius.ui.card_collection.*
import java.util.*

const val CARD_ID = "spontaniius.ui.sign_up.MESSAGE4"

class ReportUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_user)
        findViewById<Button>(R.id.report_go_back).setOnClickListener() {
            onBackPressed()
        }
        findViewById<Button>(R.id.send_report_button).setOnClickListener(){
            var reportText = findViewById<EditText>(R.id.report_text).text

            val cardExchangeDetails = JSONObject()

            cardExchangeDetails.put("cardid", intent.getStringExtra(CARD_ID))
            cardExchangeDetails.put("userid", Amplify.Auth.currentUser.userId)
            cardExchangeDetails.put("report", reportText)


            val url = "https://r3ac1f0ree.execute-api.us-west-2.amazonaws.com/default/ReportUser"
            val getLocationRequest = JsonObjectRequest(
                Request.Method.POST, url, cardExchangeDetails,
                { response ->
                    val JSONResponse = JSONObject(response.toString())
                    Toast.makeText(this, "Thank you for submitting your report, we will get to it soon, your report id is: "
                            +JSONResponse.get("reportid"), Toast.LENGTH_SHORT).show()

                    val userReported = Intent(this, BottomNavigationActivity::class.java).apply {

                    }
                    startActivity(userReported)
                },


                { error ->
                    error.printStackTrace()
                }

            )
            val queue = this?.let { VolleySingleton.getInstance(it).requestQueue }
            queue?.add(getLocationRequest)
        }
    }
}