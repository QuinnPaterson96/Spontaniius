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

        // Observe login state and handle UI updates
        viewModel.loginState.observe(viewLifecycleOwner) { result ->
            loading.visibility = View.GONE
            result.fold(
                onSuccess = { isSuccess ->
                    if (isSuccess) {
                        findNavController().navigate(R.id.bottom_navigation)
                    } else {
                        Toast.makeText(requireContext(), "Login incomplete", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Login failed: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // Login button click listener
        loginButton.setOnClickListener {
            val phoneNumber = ccp.selectedCountryCodeWithPlus + username.text.toString()
            val passwordText = password.text.toString()

            if (phoneNumber.isNotEmpty() && passwordText.isNotEmpty()) {
                loading.visibility = View.VISIBLE
                viewModel.login(phoneNumber, passwordText)
            } else {
                Toast.makeText(requireContext(), "Enter phone and password", Toast.LENGTH_SHORT).show()
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
