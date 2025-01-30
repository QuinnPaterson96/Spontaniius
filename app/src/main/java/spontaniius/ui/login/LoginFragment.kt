package spontaniius.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.amplifyframework.core.Amplify
import com.hbb20.CountryCodePicker
import com.spontaniius.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.ui.BottomNavigationActivity
import spontaniius.ui.sign_up.SignUpActivity

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.forgotPasswordButton.setOnClickListener {
            val resetIntent = Intent(requireContext(), ResetPasswordActivity::class.java).apply {
                putExtra(USERNAME, binding.username.text.toString())
            }
            startActivity(resetIntent)
        }

        binding.signupButton.setOnClickListener {
            val signUpIntent = Intent(requireContext(), SignUpActivity::class.java)
            startActivity(signUpIntent)
        }

        binding.loginButton.setOnClickListener {
            val username = binding.username.text.toString() + binding.ccp.selectedCountryCodeWithPlus
            val password = binding.password.text.toString()

            loginViewModel.login(username, password)
        }

        loginViewModel.loginState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is LoginState.Loading -> binding.loading.visibility = View.VISIBLE
                is LoginState.Success -> {
                    binding.loading.visibility = View.GONE
                    val intent = Intent(requireContext(), BottomNavigationActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish() // Close login screen
                }
                is LoginState.Error -> {
                    binding.loading.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
