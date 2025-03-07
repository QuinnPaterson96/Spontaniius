package spontaniius.ui.phone_login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.spontaniius.databinding.FragmentOtpVerificationBinding
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.ui.otp_verification.OTPVerificationViewModel
import com.spontaniius.R

@AndroidEntryPoint
class OTPVerificationFragment : Fragment() {

    private var _binding: FragmentOtpVerificationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OTPVerificationViewModel by viewModels()
    private lateinit var verificationId: String
    private lateinit var phoneNumber: String

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

        viewModel.verificationStatus.observe(viewLifecycleOwner){
            loading.visibility = View.GONE

        }

        viewModel.error.observe(viewLifecycleOwner){
            loading.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
