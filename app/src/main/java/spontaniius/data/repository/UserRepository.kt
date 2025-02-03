package spontaniius.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.exceptions.SignedOutException
import com.amplifyframework.core.Amplify
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserRepository @Inject constructor() {

    private val _userDetails = MutableLiveData<JSONObject?>()
    val userDetails: LiveData<JSONObject?> get() = _userDetails


    /**
     * Fetches the current user's attributes and updates LiveData.
     * If the user is not logged in, it sets LiveData to null.
     */
    fun fetchUserAttributes() {
        Amplify.Auth.fetchUserAttributes(
            { attributes ->
                val userDetails = parseUserAttributes(attributes)
                _userDetails.postValue(userDetails)
            },
            { error ->
                if (error is SignedOutException) {
                    Log.i("UserRepository", "User is signed out. Clearing user data.")
                } else {
                    Log.e("UserRepository", "Error fetching user attributes", error)
                }
                _userDetails.postValue(null) // Clear user details on error
            }
        )
    }

    /**
     * Parses Amplify user attributes into a structured JSONObject.
     */
    private fun parseUserAttributes(attributes: List<AuthUserAttribute>): JSONObject {
        val userId = attributes.find { it.key.keyString == "sub" }?.value ?: "Unknown"

        return JSONObject().apply {
            put("userid", userId)
            attributes.forEach { attribute ->
                when (attribute.key.keyString) {
                    "custom:cardid" -> put("cardid", attribute.value)
                    "phone_number" -> put("phone_number", attribute.value)
                    "gender" -> put("gender", attribute.value)
                    "name" -> put("name", attribute.value)
                }
            }
        }
    }
}
