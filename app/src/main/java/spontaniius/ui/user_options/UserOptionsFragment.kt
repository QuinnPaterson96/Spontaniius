package spontaniius.ui.user_options

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.ui.user_menu.UserOptionsViewModel
import java.util.*

@AndroidEntryPoint
class UserOptionsFragment : Fragment() {

    private val viewModel: UserOptionsViewModel by viewModels()

    private lateinit var nameEditText: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var saveButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_user_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        nameEditText = view.findViewById(R.id.name_edittext)
        genderRadioGroup = view.findViewById(R.id.gender_selection_radioGroup)
        saveButton = view.findViewById(R.id.save_button)
        cancelButton = view.findViewById(R.id.cancel_button)
        loadingProgressBar = view.findViewById(R.id.loading)

        val deleteButton: Button = view.findViewById(R.id.delete_account_button)

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        viewModel.accountDeleted.observe(viewLifecycleOwner) { deleted ->
            if (deleted) {
                Toast.makeText(requireContext(), "Account deleted", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.loginFragment)
            }
        }

        // Observe user data
        viewModel.user.observe(viewLifecycleOwner) { user ->
            nameEditText.setText(user?.name ?: "")

            when (user?.gender) {
                "Male" -> genderRadioGroup.check(R.id.male)
                "Female" -> genderRadioGroup.check(R.id.female)
                "Non-Binary" -> genderRadioGroup.check(R.id.non_binary)
                "Other" -> genderRadioGroup.check(R.id.other)
            }
        }

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            loadingProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            saveButton.isEnabled = !isLoading
        }

        // Save button updates user details
        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val selectedGender = when (genderRadioGroup.checkedRadioButtonId) {
                R.id.male -> "Male"
                R.id.female -> "Female"
                R.id.non_binary -> "Non-Binary"
                R.id.other -> "Other"
                else -> null
            }

            if (name.isNotEmpty() && selectedGender != null) {
                    viewModel.updateUser(name = name, phone = "", gender = selectedGender)
            } else {
            }
        }

        // Cancel button returns to previous screen
        cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Load user data initially
        viewModel.loadUser()
    }

    private fun showDeleteConfirmationDialog() {
        val input = EditText(requireContext())
        val expectedConfirmation = getString(R.string.delete_confirmation_word)
        input.hint = getString(R.string.delete_confirm_prompt, expectedConfirmation)
        expectedConfirmation.lowercase()


        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_account_dialog_title))
            .setMessage(getString(R.string.delete_account_dialog))
            .setView(input)
            .setPositiveButton(getString(R.string.delete_confirmation_word).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }) { dialog, _ ->
                if (input.text.toString().lowercase() == expectedConfirmation) {
                    viewModel.deleteUser()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.delete_confirmation_not_matched), Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(android.R.string.cancel)) { dialog, _ -> dialog.cancel() }
            .show()
    }


}
