package spontaniius.ui.sign_up_confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import com.spontaniius.R
import com.spontaniius.databinding.FragmentSignUpConfirmBinding

@AndroidEntryPoint
class SignUpConfirmFragment : Fragment() {

    private var _binding: FragmentSignUpConfirmBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpConfirmViewModel by viewModels()
    private val args: SignUpConfirmFragmentArgs by navArgs() // Get Safe Args

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = args.username
        val password = args.password

        binding.confirmButton.setOnClickListener {
            val confirmationCode = binding.confirmationCode.text.toString()
            if (confirmationCode.isNotEmpty()) {
                binding.loading.visibility = View.VISIBLE
                viewModel.confirmSignUp(username, confirmationCode, password)
            } else {
                Toast.makeText(requireContext(), "Please enter the confirmation code", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.confirmState.observe(viewLifecycleOwner) { result ->
            binding.loading.visibility = View.GONE
            result.fold(
                onSuccess = {
                    findNavController().navigate(R.id.findEventFragment) //#TODO change this back to navigating to card editing
                },
                onFailure = { error ->
                    binding.errorText.text = "Wrong authentication code"
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
