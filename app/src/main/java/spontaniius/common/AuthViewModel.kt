package spontaniius.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.repository.AuthRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // LiveData to observe authentication state
    val isUserSignedIn: LiveData<Boolean> = authRepository.isUserSignedIn

    // LiveData to track sign-out result

    // Function to check login status
    fun checkAuthState() {
        authRepository.checkAuthState()
    }

    // Function to sign out
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOutUser()
        }
    }
}
