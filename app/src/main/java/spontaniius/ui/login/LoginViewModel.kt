package spontaniius.ui.login

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import spontaniius.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    val authResult: LiveData<Boolean> = authRepository.authResult

    fun getGoogleSignInIntent(): Intent = authRepository.getGoogleSignInIntent()

    fun handleGoogleSignInResult(data: Intent?) {
        authRepository.handleGoogleSignInResult(data)
    }
}