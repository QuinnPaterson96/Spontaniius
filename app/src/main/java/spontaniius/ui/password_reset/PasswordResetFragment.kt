package com.spontaniius.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.spontaniius.R
import com.hbb20.CountryCodePicker
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.ui.password_reset.ResetPasswordViewModel

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private val viewModel: ResetPasswordViewModel by viewModels()

    private lateinit var instructionTextView: TextView
    private lateinit var errorTextView: TextView
    private lateinit var confirmationCodeEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var ccp: CountryCodePicker
    private lateinit var confirmPhoneButton: Button
    private lateinit var confirmButton: Button
    private lateinit var resendCodeButton: Button
    private lateinit var passwordSection: LinearLayout

    private var phoneNumber: String = ""
    private var passedThroughUserName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_password_reset, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI elements
        errorTextView = view.findViewById(R.id.error_text)
        instructionTextView = view.findViewById(R.id.instruction_text)
        confirmationCodeEditText = view.findViewById(R.id.confirmation_code)
        newPasswordEditText = view.findViewById(R.id.new_password)
        ccp = view.findViewById(R.id.ccp_username)
        confirmPhoneButton = view.findViewById(R.id.phoneNumberConfirm)
        confirmButton = view.findViewById(R.id.confirm_button)
        resendCodeButton = view.findViewById(R.id.resend_code)
        passwordSection = view.findViewById(R.id.password_section)

        passedThroughUserName = arguments?.getString(USERNAME)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        passwordSection.visibility = View.GONE
        ccp.visibility = View.VISIBLE
        instructionTextView.text = "Please enter your phone number"
        confirmationCodeEditText.setText(passedThroughUserName)
        confirmationCodeEditText.hint = "Phone #"

        confirmPhoneButton.setOnClickListener {
            phoneNumber = ccp.selectedCountryCodeWithPlus + confirmationCodeEditText.text.toString()
            viewModel.resetPassword(phoneNumber)
        }

        resendCodeButton.setOnClickListener {
            resetToPhoneInput()
        }

        confirmButton.setOnClickListener {
            val username = "bob" // Replace with actual username input
            val newPassword = newPasswordEditText.text.toString()
            val confirmationCode = confirmationCodeEditText.text.toString()
            viewModel.confirmResetPassword(username, newPassword, confirmationCode)
        }
    }

    private fun setupObservers() {
        viewModel.resetPasswordResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                transitionToCodeInput()
            }
        }

        viewModel.confirmPasswordResult.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Password Reset Successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.passwordResetFragment) // Navigate back to login
            }
        }

        viewModel.resetError.observe(viewLifecycleOwner) { error ->
            errorTextView.text = error
        }
    }

    private fun transitionToCodeInput() {
        ccp.visibility = View.GONE
        passwordSection.visibility = View.VISIBLE
        instructionTextView.text = "Enter the activation code"
        confirmationCodeEditText.setText("")
        confirmationCodeEditText.hint = "Activation Code"
        resendCodeButton.visibility = View.VISIBLE
        confirmPhoneButton.visibility = View.GONE
        confirmButton.visibility = View.VISIBLE
    }

    private fun resetToPhoneInput() {
        passwordSection.visibility = View.GONE
        ccp.visibility = View.VISIBLE
        instructionTextView.text = "Please enter your phone number"
        confirmationCodeEditText.setText(phoneNumber)
        confirmationCodeEditText.hint = "Phone #"
        confirmPhoneButton.visibility = View.VISIBLE
        confirmButton.visibility = View.GONE
        resendCodeButton.visibility = View.GONE
    }

    companion object {
        const val USERNAME = "username"
    }
}
