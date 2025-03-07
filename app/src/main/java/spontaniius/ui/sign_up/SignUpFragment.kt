package spontaniius.ui.sign_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import com.spontaniius.R
import com.spontaniius.databinding.FragmentSignUpBinding

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels()
    private val args: SignUpFragmentArgs by navArgs()  // ✅ Get externalId and authProvider from arguments

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userName = binding.userName
        val genderSpinner = binding.gender
        val doneButton = binding.doneButton
        //val skipButton = binding.skipButton
        val loading = binding.loading

        // Gender dropdown
        val genderOptions = arrayOf("Male", "Female", "Other")
        genderSpinner.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, genderOptions)

        // Observe sign-up result
        viewModel.signUpResult.observe(viewLifecycleOwner) { result ->
            loading.visibility = View.GONE
            result.fold(
                onSuccess = {
                    findNavController().navigate(R.id.findEventFragment)  // ✅ Navigate to Find Events
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Signup failed: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // On "Done" (proceed with sign-up)
        doneButton.setOnClickListener {
            val nameText = userName.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()

            if (nameText.isNotEmpty()) {
                loading.visibility = View.VISIBLE
                viewModel.registerUser(args.externalId, args.authProvider, nameText, gender)
            } else {
                Toast.makeText(requireContext(), "Please enter your name", Toast.LENGTH_SHORT).show()
            }
        }

        // On "Skip" (skip customization and go directly to event finding)
        /*
        skipButton.setOnClickListener {
            findNavController().navigate(R.id.findEventFragment)
        }
         */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
