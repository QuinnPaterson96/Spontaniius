package spontaniius.repository

import android.util.Log
import com.amplifyframework.core.Amplify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import spontaniius.ui.login.LoginState
import javax.inject.Inject
import kotlin.coroutines.resume

class LoginRepository @Inject constructor() {

    suspend fun login(username: String, password: String): LoginState {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                Amplify.Auth.signIn(
                    username, password,
                    { result ->
                        Log.i("AuthQuickstart", "Login successful: ${result.isSignedIn}")
                        continuation.resume(LoginState.Success)
                    },
                    { error ->
                        Log.e("AuthQuickstart", "Login failed", error)
                        continuation.resume(LoginState.Error(error.localizedMessage ?: "Login failed"))
                    }
                )
            }
        }
    }
}
