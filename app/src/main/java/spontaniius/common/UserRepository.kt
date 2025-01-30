package spontaniius.common // Ensure this matches the actual folder structure


import android.util.Log
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.exceptions.InvalidStateException
import com.amplifyframework.auth.exceptions.SignedOutException
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.core.Amplify
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class UserRepository @Inject constructor() {
    private var cachedUserDetails: JSONObject? = null


    /**
     * Fetches the current user's attributes if they are authenticated.
     * Returns a JSONObject containing the user's attributes or null if the user is not logged in.
     */
    suspend fun getCurrentUserAttributes(): JSONObject? {
        if (cachedUserDetails != null) {
            return cachedUserDetails
        }


        return try {
            val userAttributes = fetchUserAttributes()

            // Extract user ID manually from attributes
            val userId = userAttributes.find { it.key.keyString == "sub" }?.value ?: "Unknown"

            val userDetails = JSONObject().apply {
                put("userid", userId) // Use extracted ID

                userAttributes.forEach { attribute ->
                    when (attribute.key.keyString) {
                        "custom:cardid" -> put("cardid", attribute.value)
                        "phone_number" -> put("phone_number", attribute.value)
                        "gender" -> put("gender", attribute.value)
                        "name" -> put("name", attribute.value)
                    }
                }
            }

            cachedUserDetails = userDetails
            userDetails
        }catch (e: SignedOutException){
            Log.i("UserRepository", "User Not Logged in", e)
            null
        }
        catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user attributes", e)
            null
        }
    }

    /**
     * Fetches user attributes asynchronously.
     * Will fail if the user is not signed in.
     */
    private suspend fun fetchUserAttributes(): List<AuthUserAttribute> =
        suspendCancellableCoroutine { continuation ->
            Amplify.Auth.fetchUserAttributes(
                { attributes ->
                    continuation.resume(attributes)
                },
                { error ->
                    Log.e("UserRepository", "Error fetching user attributes", error)
                    continuation.resumeWithException(error)
                }
            )
        }

}

