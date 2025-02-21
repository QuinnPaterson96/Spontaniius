package spontaniius.data.repository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import spontaniius.data.local.dao.UserDao
import spontaniius.data.local.entities.UserEntity
import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.UserResponse
import spontaniius.domain.models.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource, // ✅ Remote API
    private val userDao: UserDao // ✅ Local Storage
) {
    private val _userDetails = MutableLiveData<UserResponse?>()
    val userDetails: LiveData<UserResponse?> get() = _userDetails

    /**
     * Fetches user details from the API and updates local database + LiveData.
     */
    suspend fun fetchUserDetails(userId: String? = null, externalId: String? = null): Result<UserResponse> {
        if (userId == null && externalId == null) {
            return Result.failure(Exception("Either userId or externalId must be provided"))
        }

        return remoteDataSource.getUser(userId, externalId).also { result ->
            result.onSuccess { user ->
                _userDetails.postValue(user) // ✅ Update LiveData on success
                saveUserLocally(user) // ✅ Store in Room Database
            }
        }
    }

    /**
     * Saves user details locally in Room database.
     */
    private suspend fun saveUserLocally(user: UserResponse) = withContext(Dispatchers.IO) {
        val userEntity = UserEntity(
            id = user.id,
            name = user.name,
            phone = user.phone,
            gender = user.gender,
            card_id = user.card_id,
            external_id = user.external_id,
            auth_provider = user.auth_provider
        )
        userDao.insertUser(userEntity)
    }

    /**
     * Retrieves user details from local database.
     */
    suspend fun getUserFromLocal(): User? = withContext(Dispatchers.IO) {
        userDao.getUser()?.toDomainModel() // No explicit `return`
    }

    /**
     * Clears stored user details when logging out.
     */
    suspend fun clearUser() = withContext(Dispatchers.IO) {
        userDao.clearUser()
        _userDetails.postValue(null) // ✅ Also clear LiveData
    }

    suspend fun getUserCardId(): Int? = withContext(Dispatchers.IO) {
        userDao.getUser()?.card_id
    }

    suspend fun getUserId(): String? {
        return userDao.getUser()?.id
    }

}


