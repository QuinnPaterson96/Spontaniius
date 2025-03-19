package spontaniius.ui.login


import com.spontaniius.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.spontaniius.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import spontaniius.data.model.AuthProvider
import spontaniius.ui.phone_login.OTPVerificationFragmentDirections

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var external_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth // Initialize Firebase Auth here
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        credentialManager = CredentialManager.create(requireContext())


        val phoneSignInButton = view.findViewById<Button>(R.id.btn_phone_login)
        phoneSignInButton.setOnClickListener {
            findNavController().navigate(R.id.phoneLoginFragment)
        }

        val googleSignInButton = view.findViewById<Button>(R.id.btn_google_signin)
        googleSignInButton.setOnClickListener {
            launchGoogleSignIn()
        }

        viewModel.newUser.observe(viewLifecycleOwner){ newUser ->
            if (newUser){
                // New user so navigate to get some extra details
                val action = LoginFragmentDirections
                    .actionLoginFragmentToSignupFragment(
                        externalId = external_id,
                        authProvider = AuthProvider.GOOGLE.providerName
                    )
                findNavController().navigate(action)
            }else{
                // Existing user so go to find event fragment
                findNavController().navigate(R.id.findEventFragment)
            }
        }

        return view
    }

    private fun launchGoogleSignIn() {
        // Instantiate a Google sign-in request

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false)
            .build()

        // Create the Credential Manager request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        // [END create_credential_manager_request]

        lifecycleScope.launch {
            try {
                // Launch Credential Manager UI
                val result = credentialManager.getCredential(
                    context = requireContext(),
                    request = request
                )

                // Extract credential from the result returned by Credential Manager
                handleSignIn(result.credential)
            } catch (e: GetCredentialException) {
                Log.e(TAG, "Couldn't retrieve user's credentials: ${e.localizedMessage}")
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->  // Use requireActivity() for Fragment
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val user = auth.currentUser
                    if (user != null) {
                        external_id = user.uid
                        viewModel.checkNewUser(user.uid)
                    }

                } else {
                    // If sign in fails, display a message to the user
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Google Sign-In failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }


    private fun handleSignIn(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

            // 🔹 Log the ID token to debug
            Log.d(TAG, "Google ID Token: ${googleIdTokenCredential.idToken}")

            // 🔹 Ensure token is not null before proceeding
            if (googleIdTokenCredential.idToken.isNullOrEmpty()) {
                Log.e(TAG, "Received empty Google ID Token!")
                return
            }

            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }



    private val TAG = "GoogleFragment"

}

