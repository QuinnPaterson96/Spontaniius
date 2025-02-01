package spontaniius.data.repository

import android.util.Log
import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.core.Amplify
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONException
import org.json.JSONObject
import spontaniius.di.VolleySingleton
import javax.inject.Inject
import kotlin.coroutines.resume

class CardRepository @Inject constructor(
    private val volleySingleton: VolleySingleton
) {

    suspend fun createCard(userId: String?, name: String?, backgroundId: Int, phone: String?): Result<Boolean> {
        if (userId == null || name == null || phone == null) {
            return Result.failure(Exception("Invalid user data"))
        }

        return suspendCancellableCoroutine { continuation ->
            val url = "https://1j8ss7fj13.execute-api.us-west-2.amazonaws.com/default/createCard"
            val cardObject = JSONObject()
            try {
                cardObject.put("userid", userId)
                cardObject.put("cardtext", name)
                cardObject.put("background", backgroundId)
                cardObject.put("backgroundAddress", "")
                cardObject.put("phone", phone)
                cardObject.put("greeting", name)
            } catch (e: JSONException) {
                continuation.resume(Result.failure(Exception("Error creating card JSON")))
                return@suspendCancellableCoroutine
            }

            val createUserRequest = JsonObjectRequest(
                Request.Method.POST, url, cardObject,
                { response ->
                    try {
                        val cardId = response.getInt("cardid")
                        val cardAttribute = AuthUserAttribute(AuthUserAttributeKey.custom("custom:cardid"), cardId.toString())
                        Amplify.Auth.updateUserAttribute(cardAttribute,
                            { Log.i("CardRepository", "Updated user attribute = $it") },
                            { Log.e("CardRepository", "Failed to update user attribute", it) }
                        )
                        continuation.resume(Result.success(true))
                    } catch (e: JSONException) {
                        continuation.resume(Result.failure(Exception("Failed to parse server response")))
                    }
                },
                { error ->
                    continuation.resume(Result.failure(Exception("Failed to create card: ${error.message}")))
                }
            )
            volleySingleton.requestQueue.add(createUserRequest)
        }
    }
}
