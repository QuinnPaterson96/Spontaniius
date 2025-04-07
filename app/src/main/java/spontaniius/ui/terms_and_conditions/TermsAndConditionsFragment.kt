package spontaniius.ui.terms_and_conditions

import androidx.fragment.app.viewModels

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import androidx.core.view.isVisible

@AndroidEntryPoint
class TermsAndConditionsFragment : Fragment() {

    private val viewModel: TermsAndConditionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val view = inflater.inflate(R.layout.fragment_terms_and_conditions, container, false)

        val termsTextView: TextView = view.findViewById(R.id.text_terms)
        val privacyPolicyToggle: TextView = view.findViewById(R.id.toggle_privacy_policy)
        val privacyPolicyText: TextView = view.findViewById(R.id.privacy_policy_text)
        val acceptButton: Button = view.findViewById(R.id.button_accept)

        // Load content
        val termsText = readAssetFile("terms_and_conditions.txt")
        val privacyText = readAssetFile("privacy_policy.txt")

        termsTextView.text = termsText
        privacyPolicyText.text = privacyText

        // Expand/collapse logic
        privacyPolicyToggle.setOnClickListener {
            val isVisible = privacyPolicyText.isVisible
            privacyPolicyText.visibility = if (isVisible) View.GONE else View.VISIBLE
            privacyPolicyToggle.text = if (isVisible) "View Privacy Policy" else "Hide Privacy Policy"
        }

        acceptButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.acceptTermsAndConditions()
            }
        }

        viewModel.acceptanceState.observe(viewLifecycleOwner) {
            val action = TermsAndConditionsFragmentDirections
                .actionTermsAndConditionsFragmentToCardEditingFragment(newUser = true)
            findNavController().navigate(action)
        }

        return view
    }


    private fun readAssetFile(filename: String): String {
        val inputStream = requireContext().assets.open(filename)
        val reader = BufferedReader(InputStreamReader(inputStream))
        return reader.readText()
    }
}
