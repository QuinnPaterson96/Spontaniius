package spontaniius.ui.report_user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import com.spontaniius.R

@AndroidEntryPoint
class ReportUserFragment : Fragment() {

    private val viewModel: ReportUserViewModel by viewModels()
    private val args: ReportUserFragmentArgs by navArgs()
    private var cardId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_report_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reportText = view.findViewById<EditText>(R.id.report_text)
        val goBackButton = view.findViewById<Button>(R.id.report_go_back)
        val sendReportButton = view.findViewById<Button>(R.id.send_report_button)

        val cardId = arguments?.getInt("CARD_ID") ?: return
        val userId = arguments?.getString("USER_ID") ?: return

        goBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        sendReportButton.setOnClickListener {
            val reportMessage = reportText.text.toString().trim()
            if (reportMessage.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a report message.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.submitReport(userId, cardId, reportMessage)
            }
        }

        // Observe ViewModel
        viewModel.reportResult.observe(viewLifecycleOwner) { report ->
            Toast.makeText(
                requireContext(),
                "Report Submitted! Report ID: ${report.reportId}\n" +
                        "Reported Card ID: ${report.reportedId}\n" +
                        "Created At: ${report.createdAt}",
                Toast.LENGTH_LONG
            ).show()
            findNavController().navigateUp() // Go back after submission
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_LONG).show()
        }
    }
}
