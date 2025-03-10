package spontaniius.ui.phone_login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hbb20.CountryCodePicker
import dagger.hilt.android.AndroidEntryPoint
import com.spontaniius.R
import com.spontaniius.databinding.FragmentPhoneLoginBinding

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

        val phoneInput = binding.username
        val verifyButton = binding.loginButton
        val loading = binding.loading
        val ccp = binding.ccp
        val phoneErrorText = binding.phoneError


        verifyButton.setOnClickListener {
            val phoneNumber = ccp.selectedCountryCodeWithPlus + phoneInput.text.toString().trim()

            // Reset previous errors
            phoneErrorText.visibility = View.GONE

            if (phoneNumber.isEmpty() || phoneNumber.length < 10) {
                phoneErrorText.text = getString(R.string.invalid_phone_number)
                phoneErrorText.visibility = View.VISIBLE
                return@setOnClickListener
            }

            loading.visibility = View.VISIBLE

            viewModel.sendOtp(phoneNumber, requireActivity(), { result ->
                loading.visibility = View.GONE
                result.onSuccess { verificationId ->
                    // ✅ Navigate to OTP verification screen with the verificationId
                    val action = PhoneLoginFragmentDirections
                        .actionPhoneLoginFragmentToOTPVerificationFragment(verificationId, phoneNumber)
                    findNavController().navigate(action)
                }.onFailure { error ->
                    Toast.makeText(requireContext(), "Error: ${error.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
