package spontaniius.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.spontaniius.BuildConfig
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import spontaniius.data.model.AuthProvider
import spontaniius.ui.phone_login.OTPVerificationFragmentDirections

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var oneTapClient: SignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        oneTapClient = Identity.getSignInClient(requireContext()) // ✅ New Sign-In API

        val phoneSignInButton = view.findViewById<Button>(R.id.btn_phone_login)
        phoneSignInButton.setOnClickListener {
            findNavController().navigate(R.id.phoneLoginFragment)
        }

        val googleSignInButton = view.findViewById<Button>(R.id.btn_google_signin)
        googleSignInButton.setOnClickListener {
            launchGoogleSignIn()
        }

        return view
    }


    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(WEB_CLIENT_ID)
        .setAutoSelectEnabled(true)
        .setNonce(<nonce string to use when generating a Google ID token>)
    .build()

    private fun launchGoogleSignIn() {
        val signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID) // ✅ Use Web Client ID
                    .setFilterByAuthorizedAccounts(false) // Set true if you want to use only already signed-in accounts
                    .build()
            )
            .build()

        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(requireActivity()) { result ->
                googleSignInLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent).build())
            }
            .addOnFailureListener(requireActivity()) { e ->
                Toast.makeText(requireContext(), "Google Sign-In failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    if (idToken != null) {
                        viewModel.handleGoogleSignInResult(idToken)
                    } else {
                        Toast.makeText(requireContext(), "Google Sign-In failed: ID Token is null", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Google Sign-In failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Google Sign-In cancelled", Toast.LENGTH_SHORT).show()
            }
        }
}

