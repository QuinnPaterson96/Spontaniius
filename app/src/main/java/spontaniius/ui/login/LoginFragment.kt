package spontaniius.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.spontaniius.BuildConfig
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import spontaniius.data.model.AuthProvider

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

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
            viewLifecycleOwner.lifecycleScope.launch {
                googleSignInButton.isEnabled = false
                phoneSignInButton.isEnabled = false
                try {
                    signInWithGoogle(view)
                } finally {
                    googleSignInButton.isEnabled = true
                    phoneSignInButton.isEnabled = true
                }
            }
        }

        val viewPager = view.findViewById<ViewPager2>(R.id.view_pager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        val images = listOf(R.drawable.onboarding1, R.drawable.onboarding2, R.drawable.onboarding3)
        viewPager.adapter = OnboardingAdapter(images)

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        return view
    }

    private suspend fun signInWithGoogle(view: View) {
        val option = GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_CLIENT_ID).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        val token = try {
            val credential = credentialManager.getCredential(requireActivity(), request).credential
            if (credential !is CustomCredential || credential.type != TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                showGoogleError()
                return
            }
            GoogleIdTokenCredential.createFrom(credential.data).idToken
        } catch (_: GetCredentialCancellationException) {
            return
        } catch (_: GetCredentialException) {
            showGoogleError()
            return
        } catch (_: GoogleIdTokenParsingException) {
            showGoogleError()
            return
        }

        val uid = viewModel.signInWithGoogle(token).getOrElse {
            showGoogleError()
            return
        }
        loadProfile(view, uid)
    }

    private fun showGoogleError() {
        context?.let { Toast.makeText(it, R.string.google_sign_in_failed, Toast.LENGTH_LONG).show() }
    }

    private suspend fun loadProfile(view: View, uid: String) {
        if (auth.currentUser?.uid != uid) return
        val profile = viewModel.lookupProfile(uid)
        if (auth.currentUser?.uid != uid || findNavController().currentDestination?.id != R.id.loginFragment) return
        when (profile) {
            ProfileLookup.EXISTING -> findNavController().navigate(R.id.findEventFragment)
            ProfileLookup.NEW -> findNavController().navigate(
                LoginFragmentDirections.actionLoginFragmentToSignupFragment(uid, AuthProvider.GOOGLE.providerName)
            )
            ProfileLookup.UNAVAILABLE -> Snackbar.make(view, R.string.sign_in_profile_unavailable, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry_profile_load) {
                    viewLifecycleOwner.lifecycleScope.launch { loadProfile(view, uid) }
                }.show()
        }
    }
}
