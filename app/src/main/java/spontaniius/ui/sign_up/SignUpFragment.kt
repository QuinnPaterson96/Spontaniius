package spontaniius.ui.sign_up

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
import com.spontaniius.databinding.FragmentSignUpBinding
import com.hbb20.CountryCodePicker

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = binding.userName
        val phone = binding.phoneNumber
        val password = binding.password
        val genderSpinner = binding.gender
        val signUpButton = binding.doneButton
        val loginButton = binding.loginButton
        val loading = binding.loading
        val ccp = binding.ccp
        val errorText = binding.errorText

        // Gender dropdown
        val genderOptions = arrayOf("Male", "Female", "Other")
        genderSpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderOptions)

        viewModel.signUpState.observe(viewLifecycleOwner) { result ->
            loading.visibility = View.GONE
            result.fold(
                onSuccess = { userId ->
                    val action = SignUpFragmentDirections
                        .actionSignUpFragmentToSignUpConfirmFragment(username, password)

                    findNavController().navigate(action)
                },
                onFailure = { error ->
                    errorText.text = when {
                        error.message?.contains("password", ignoreCase = true) == true ->
                            "Your password must be 6 characters long, with at least one capital letter and one number."
                        error.message?.contains("phone", ignoreCase = true) == true ->
                            "Phone number isn't valid."
                        else -> "Sign up failed. Please try again."
                    }
                }
            )
        }

        signUpButton.setOnClickListener {
            val nameText = username.text.toString()
            val phoneText = phone.text.toString()
            val passwordText = password.text.toString()
            val gender = genderSpinner.selectedItem.toString()

            if (nameText.isNotEmpty() && phoneText.isNotEmpty() && passwordText.isNotEmpty()) {
                loading.visibility = View.VISIBLE
                viewModel.signUp(nameText, gender, ccp.selectedCountryCodeWithPlus + phoneText, passwordText)
            } else {
                Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        loginButton.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
