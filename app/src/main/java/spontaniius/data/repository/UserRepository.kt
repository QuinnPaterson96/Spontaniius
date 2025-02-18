package spontaniius.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.UserResponse
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource // ✅ Use RemoteDataSource instead of ApiService
) {
    private val _userDetails = MutableLiveData<UserResponse?>()
    val userDetails: LiveData<UserResponse?> get() = _userDetails

    /**
     * Fetches user details from the API and updates LiveData.
     */
    suspend fun fetchUserDetails(userId: String? = null, externalId: String? = null): Result<UserResponse> {
        if (userId == null && externalId == null) {
            return Result.failure(Exception("Either userId or externalId must be provided"))
        }

        return remoteDataSource.getUser(userId, externalId).also { result ->
            result.onSuccess { user ->
                _userDetails.postValue(user) // ✅ Update LiveData on success
            }
        }
    }
}
