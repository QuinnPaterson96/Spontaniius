package spontaniius.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.result.AuthResetPasswordResult
import com.amplifyframework.core.Amplify
import com.rilixtech.widget.countrycodepicker.CountryCodePicker
import com.spontaniius.R
import spontaniius.ui.sign_up.USER_ID

const val USERNAME = "spontaniius.ui.sign_up.MESSAGE5"

class ResetPasswordActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        var ccp: CountryCodePicker


        var passedThroughUserName = intent.getStringExtra(USERNAME)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        var errorTextView = findViewById<TextView>(R.id.error_text)
        var instructionTextView = findViewById<TextView>(R.id.instruction_text)
        val back = findViewById<Button>(R.id.back_button)
        val confirm = findViewById<Button>(R.id.confirm_button)
        val confirmPhoneButton = findViewById<Button>(R.id.phoneNumberConfirm)

        val confirmationCodeEditText = findViewById<EditText>(R.id.confirmation_code)
        val confirmationCodeLabel = findViewById<TextView>(R.id.confirmation_section_label)
        val passwordSection = findViewById<LinearLayout>(R.id.password_section)
        val newPasswordEditText:EditText = findViewById(R.id.new_password)
        val resendCodeButton:Button=findViewById(R.id.resend_code)
        ccp =  findViewById(R.id.ccp_username);
        var phoneNumber = ""
        back.setOnClickListener {
            val intent2 = Intent(this, LoginActivity::class.java).apply {}
            startActivity(intent2)
        }




                // We assume here that the reason reset password failed is due to not having the right /
                // any phone number, so we prompt user to re-enter

         passwordSection.visibility= GONE
         ccp.visibility= VISIBLE // This is needed in order to help user set phone number
         confirmationCodeLabel.visibility= GONE
         instructionTextView.text="Please enter your phone number"
         confirmationCodeEditText.setText(passedThroughUserName)
         confirmationCodeEditText.hint = "phone #"




                confirmPhoneButton.setOnClickListener {
                    phoneNumber=confirmationCodeEditText.text.toString()
                    Amplify.Auth.resetPassword(
                        ccp.selectedCountryCodeWithPlus+confirmationCodeEditText.text.toString(),
                        { result: AuthResetPasswordResult ->
                            Log.i(
                                "AuthQuickstart",
                                result.toString()
                            )

                            ccp.visibility= GONE
                            passwordSection.visibility= VISIBLE
                            confirmationCodeLabel.visibility= VISIBLE
                            confirmationCodeLabel.text="Activation Code"
                            confirmationCodeEditText.hint = "Activation Code"
                            confirmationCodeEditText.setText("")
                            resendCodeButton.visibility= VISIBLE

                            resendCodeButton.setOnClickListener {
                                passwordSection.visibility= GONE
                                ccp.visibility= VISIBLE // This is needed in order to help user set phone number
                                confirmationCodeLabel.visibility= GONE
                                instructionTextView.text="Please enter your phone number"
                                confirmationCodeEditText.setText(phoneNumber)
                                confirmationCodeEditText.hint = "phone #"
                                confirmPhoneButton.visibility= VISIBLE
                                confirm.visibility= GONE
                                resendCodeButton.visibility = GONE
                                confirmPhoneButton.visibility= VISIBLE


                            }
                            confirmPhoneButton.visibility= GONE
                            confirm.visibility= VISIBLE

                            confirm.setOnClickListener {
                                Amplify.Auth.confirmResetPassword(
                                    newPasswordEditText.text.toString(),
                                    confirmationCodeEditText.text.toString(),
                                    {
                                        Log.i(
                                            "AuthQuickstart",
                                            "New password confirmed"
                                        )
                                        val toast = Toast.makeText(applicationContext, "Password Reset", Toast.LENGTH_SHORT)
                                        toast.show()
                                        val intent2 = Intent(this, LoginActivity::class.java).apply {}
                                        startActivity(intent2)

                                    }
                                ) { error: AuthException ->
                                    Log.e(
                                        "AuthQuickstart",
                                        error.toString()
                                    )
                                    errorTextView.text = error.toString()
                                }
                            }

                        }
                    ) { error: AuthException ->
                        Log.e(
                            "AuthQuickstart",
                            error.toString()
                        )
                        errorTextView.text= "Try providing your phone number again"
                        confirmationCodeEditText.hint = "phone #"
                        confirmationCodeEditText.setText("")

                    }
                }


            }
 }

