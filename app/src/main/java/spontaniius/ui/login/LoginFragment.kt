package spontaniius.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.spontaniius.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private val RC_SIGN_IN = 100 // Request code for Google Sign-In

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Google Sign-In button
        val googleSignInButton = view.findViewById<Button>(R.id.btn_google_signin)
        googleSignInButton.setOnClickListener {
            val signInIntent = viewModel.getGoogleSignInIntent()
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        val phoneSignInButton = view.findViewById<Button>(R.id.btn_phone_login)
        phoneSignInButton.setOnClickListener {
            findNavController().navigate(R.id.phoneLoginFragment)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            viewModel.handleGoogleSignInResult(data, requireActivity())
        }
    }
}
