package spontaniius.ui.phone_login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.spontaniius.R
import com.spontaniius.databinding.FragmentOtpVerificationBinding
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.data.model.AuthProvider
import spontaniius.ui.otp_verification.OTPVerificationViewModel

@AndroidEntryPoint
class OTPVerificationFragment : Fragment() {

    private var _binding: FragmentOtpVerificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OTPVerificationViewModel by viewModels()
    private lateinit var verificationId: String
    private lateinit var phoneNumber: String
    private lateinit var external_id: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        verificationId = arguments?.getString("verificationId") ?: ""
        phoneNumber = arguments?.getString("phoneNumber") ?: ""

        val otpInput = binding.otpInput
        val verifyButton = binding.verifyOtpButton
        val loading = binding.loadingIndicator

        verifyButton.setOnClickListener {
            val otpCode = otpInput.text.toString().trim()
            if (otpCode.length != 6) {
                Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loading.visibility = View.VISIBLE

            viewModel.verifyOtp(verificationId, otpCode, requireActivity())

        }

        viewModel.firebaseId.observe(viewLifecycleOwner){ id ->
            loading.visibility = View.GONE
            external_id = id
            viewModel.checkNewUser(id)

        }

        viewModel.error.observe(viewLifecycleOwner){
            loading.visibility = View.GONE
        }

        viewModel.newUser.observe(viewLifecycleOwner){ newUser ->
            if (newUser){
                // New user so navigate to get some extra details
                val action = OTPVerificationFragmentDirections
                    .actionOTPVerificationFragmentToSignupFragment(
                        externalId = external_id,  // Replace with actual value
                        authProvider = AuthProvider.FIREBASE.providerName
                    )
                findNavController().navigate(action)
            }else{

                // Existing user so go to find event fragment
                findNavController().navigate(R.id.findEventFragment)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
