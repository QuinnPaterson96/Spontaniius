package spontaniius.data.remote

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Adds the current Firebase ID token only to requests sent through the backend client. */
class FirebaseIdTokenInterceptor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val identity = chain.request().tag(AccountDeletionIdentity::class.java)
        val user = firebaseAuth.currentUser
        if (identity != null) {
            if (user != null && user.uid != identity.uid) throw IOException("Account identity changed")
            if (identity.signedToken.isBlank()) throw IOException("Firebase ID token unavailable")
            return chain.proceed(chain.request().newBuilder()
                .header("Authorization", "Bearer ${identity.signedToken}").build())
        }
        if (user == null) return chain.proceed(chain.request())
        val token = try {
            // Firebase reuses a valid cached token and refreshes it when needed.
            Tasks.await(user.getIdToken(false), 10, TimeUnit.SECONDS).token
        } catch (exception: InterruptedException) {
            Thread.currentThread().interrupt()
            throw IOException("Firebase ID token retrieval interrupted", exception)
        } catch (exception: Exception) {
            throw IOException("Firebase ID token unavailable", exception)
        }

        if (token.isNullOrBlank()) {
            throw IOException("Firebase ID token unavailable")
        }

        return chain.proceed(
            chain.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        )
    }
}
