package spontaniius.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import spontaniius.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {


    fun handleGoogleSignInResult(data: Intent?, activity: Activity) {
        viewModelScope.launch {
            authRepository.handleGoogleSignInResult(data, activity)
        }
    }
}