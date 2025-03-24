package spontaniius.ui.user_options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import spontaniius.ui.user_menu.UserOptionsViewModel

@AndroidEntryPoint
class UserOptionsFragment : Fragment() {

    private val viewModel: UserOptionsViewModel by viewModels()

    private lateinit var nameEditText: EditText
    private lateinit var genderRadioGroup: RadioGroup
    private lateinit var saveButton: FloatingActionButton
    private lateinit var cancelButton: FloatingActionButton
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
}
