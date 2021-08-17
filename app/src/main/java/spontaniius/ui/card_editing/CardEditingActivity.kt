package spontaniius.ui.card_editing

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.core.Amplify
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_card_editing.*
import org.json.JSONException
import org.json.JSONObject
import spontaniius.R
import spontaniius.dependency_injection.VolleySingleton
import spontaniius.ui.BottomNavigationActivity
import spontaniius.ui.find_event.EventFindAdapter
import spontaniius.ui.sign_up.PHONE_NUMBER
import spontaniius.ui.sign_up.USER_ID
import spontaniius.ui.sign_up.USER_NAME


class CardEditingActivity : AppCompatActivity(){

    lateinit var greetingView: TextView
    lateinit var selectedCardBackground: ImageView
    lateinit var cardGreetingEdit: EditText
    var backgroundID = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_editing)

        var cardbackgrounds = arrayOf(R.drawable.card_gold, R.drawable.card_bubbles, R.drawable.card_sunrise,
                                      R.drawable.card_ocean, R.drawable.card_rose, R.drawable.card_trees,  R.drawable.card_circuit)




        val name = intent.getStringExtra(USER_NAME)
        findViewById<TextView>(R.id.selected_card_user_name).text = name
        greetingView = findViewById<TextView>(R.id.selected_card_greeting)
        greetingView.text = findViewById<EditText>(R.id.card_greeting_edit).text
        selectedCardBackground=findViewById(R.id.selected_card_background)
        selectedCardBackground.setImageResource(cardbackgrounds[0])
        backgroundID = cardbackgrounds[0]
        var selectedCardGreeting = findViewById<TextView>(R.id.selected_card_greeting)


        val back2 = findViewById<Button>(R.id.back_button2)

        val phone = intent.getStringExtra(PHONE_NUMBER)
        val userid = intent.getStringExtra(USER_ID)
        val queue = VolleySingleton.getInstance(this.applicationContext).requestQueue
        val done3 = findViewById<Button>(R.id.done_button3)

        backgroundID = cardbackgrounds[0]

        findViewById<Button>(R.id.card0).setOnClickListener {
            backgroundID= cardbackgrounds[0]
            selectedCardBackground.setImageResource(backgroundID)
        }

        findViewById<Button>(R.id.card1).setOnClickListener {
            backgroundID= cardbackgrounds[1]
            selectedCardBackground.setImageResource(backgroundID)
        }
        findViewById<Button>(R.id.card2).setOnClickListener {
            backgroundID= cardbackgrounds[2]
            selectedCardBackground.setImageResource(backgroundID)
        }
        findViewById<Button>(R.id.card3).setOnClickListener {
            backgroundID= cardbackgrounds[3]
            selectedCardBackground.setImageResource(backgroundID)
        }
        findViewById<Button>(R.id.card4).setOnClickListener {
            backgroundID= cardbackgrounds[4]
            selectedCardBackground.setImageResource(backgroundID)
        }
        findViewById<Button>(R.id.card5).setOnClickListener {
            backgroundID= cardbackgrounds[5]
            selectedCardBackground.setImageResource(backgroundID)
        }
        findViewById<Button>(R.id.card6).setOnClickListener {
            backgroundID= cardbackgrounds[6]
            selectedCardBackground.setImageResource(backgroundID)
        }




        back2.setOnClickListener{
            val intent4 = Intent(this, BottomNavigationActivity::class.java)
            startActivity(intent4)
        }


        done3.setOnClickListener {
            val url = "https://1j8ss7fj13.execute-api.us-west-2.amazonaws.com/default/createCard"
            val cardObject = JSONObject()
            try {
                cardObject.put("userid", userid)
                cardObject.put("cardtext", name)
                cardObject.put("background", backgroundID)
                cardObject.put("backgroundAddress","")
                cardObject.put("phone", phone)
                cardObject.put("greeting", selectedCardGreeting.text)

            } catch (e: JSONException) {
                // handle exception
            }
            val createUserRequest = JsonObjectRequest(
                Request.Method.POST, url, cardObject,
                { response ->
                    val cardid = JSONObject(response.toString()).getInt("cardid")
                    val cardAttribute= AuthUserAttribute(AuthUserAttributeKey.custom("custom:cardid"), cardid.toString())
                    Amplify.Auth.updateUserAttribute(cardAttribute,
                        { Log.i("AuthDemo", "Updated user attribute = $it") },
                        { Log.e("AuthDemo", "Failed to update user attribute =$it") }
                    )
                    val intent = Intent(this, BottomNavigationActivity::class.java).apply {

                    }
                    startActivity(intent)
                },
                { error ->
                    Toast.makeText(this, "err" + error.toString(), Toast.LENGTH_LONG).show()
                }
            )
            queue.add(createUserRequest)

        }

        cardGreetingEdit = findViewById(R.id.card_greeting_edit)
        cardGreetingEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var currentGreeting = s.toString()
                selectedCardGreeting.setText(currentGreeting)

            }
        })

        selectedCardGreeting.setText(cardGreetingEdit.text)

    }

}
