package spontaniius.ui.phone_login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import com.spontaniius.R
import com.spontaniius.databinding.FragmentPhoneLoginBinding
import spontaniius.ui.login.PhoneLoginViewModel


@AndroidEntryPoint
class PhoneLoginFragment : Fragment() {

    private var _binding: FragmentPhoneLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PhoneLoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = binding.username
        val password = binding.password
        val signupButton = binding.signupButton
        val loginButton = binding.loginButton
        val loading = binding.loading
        val forgotPasswordButton = binding.forgotPasswordButton
        val ccp = binding.ccp

        val phoneErrorText = binding.phoneError
        val passwordErrorText = binding.passwordError

        // ✅ Observe only login errors
        viewModel.loginError.observe(viewLifecycleOwner) { error ->
            loading.visibility = View.GONE
            if (error != null) {
                passwordErrorText.text = "Login failed: ${error.message}"
                passwordErrorText.visibility = View.VISIBLE
            }
        }

        // Login button click listener
        loginButton.setOnClickListener {
            val phoneNumber = ccp.selectedCountryCodeWithPlus + username.text.toString().trim()
            val passwordText = password.text.toString().trim()

            // Reset previous errors
            phoneErrorText.visibility = View.GONE
            passwordErrorText.visibility = View.GONE

            var isValid = true

            if (phoneNumber.isEmpty() || phoneNumber.length < 10) {
                phoneErrorText.text = "Invalid phone number"
                phoneErrorText.visibility = View.VISIBLE
                isValid = false
            }

            if (passwordText.length < 6) {
                passwordErrorText.text = "Password must be at least 6 characters"
                passwordErrorText.visibility = View.VISIBLE
                isValid = false
            }

            if (isValid) {
                loading.visibility = View.VISIBLE
                viewModel.login(phoneNumber, passwordText)
            }
        }

        // Navigate to SignUpFragment
        signupButton.setOnClickListener {
            findNavController().navigate(R.id.signupFragment)
        }

        // Navigate to ForgotPasswordFragment
        forgotPasswordButton.setOnClickListener {
            findNavController().navigate(R.id.passwordResetFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
