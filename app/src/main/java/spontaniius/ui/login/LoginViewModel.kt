package spontaniius.ui.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.HttpException
import spontaniius.data.repository.AuthRepository
import spontaniius.data.repository.UserRepository
import javax.inject.Inject

enum class ProfileLookup { EXISTING, NEW, UNAVAILABLE }

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    suspend fun signInWithGoogle(idToken: String): Result<String> =
        authRepository.handleGoogleSignInResult(idToken)

    suspend fun lookupProfile(uid: String): ProfileLookup {
        val result = userRepository.fetchUserDetails(externalId = uid)
        if (result.isSuccess) return ProfileLookup.EXISTING
        val error = result.exceptionOrNull()
        return if (error is HttpException && error.code() == 404) ProfileLookup.NEW else ProfileLookup.UNAVAILABLE
    }
}
