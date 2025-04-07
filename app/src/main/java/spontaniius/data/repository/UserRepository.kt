package spontaniius.data.repository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import spontaniius.data.local.dao.UserDao
import spontaniius.data.local.entities.UserEntity
import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.CreateUserRequest
import spontaniius.data.remote.models.UpdateUserCardRequest
import spontaniius.data.remote.models.UpdateUserFCMTokenRequest
import spontaniius.data.remote.models.UpdateUserRequest
import spontaniius.data.remote.models.UserResponse
import spontaniius.domain.models.User
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource, // ✅ Remote API
    private val userDao: UserDao // ✅ Local Storage
) {
    private val _userDetails = MutableLiveData<UserResponse?>()

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
        clearUser()
        val userEntity = UserEntity(
            id = user.id,
            name = user.name,
            phone = user.phone,
            gender = user.gender,
            card_id = user.card_id,
            external_id = user.external_id,
            auth_provider = user.auth_provider,
            terms_accepted = user.terms_accepted
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

    suspend fun getUserDetails():  UserEntity? {
        return userDao.getUser()
    }

    suspend fun createUser(request: CreateUserRequest): Result<UserResponse> {
         val response = remoteDataSource.createUser(request)
          response.onSuccess { result ->
              _userDetails.postValue(result)
              saveUserLocally(result)
          }
        return response
    }

    suspend fun updateUser(name: String?, phone: String?, gender: String?): Result<UserResponse> {
        return try {
            // Fetch current user to retain unchanged fields
            val currentUserResult = userDao.getUser()
            val currentUser = currentUserResult!!.toDomainModel()

            // Merge old and new values

            val updatedUser = UpdateUserRequest(
                name = name ?: currentUser.name,
                phone = phone ?: currentUser.phone,
                gender = gender ?: currentUser.gender,
                cardId = currentUser.cardId.toString(),
            )

            // Send update request
            remoteDataSource.updateUser(currentUser.id, updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserCard(cardId: Int): Result<UserResponse>{
        val request = UpdateUserCardRequest(
            user_id = getUserId()!!, card_id = cardId
        )
        val user = userDao.getUser()
        if (user != null) {
            user.card_id = cardId
        }
        if (user != null) {
            userDao.insertUser(user)
        }

        return remoteDataSource.updateUserCard(request)
    }

    suspend fun updateUserFCMToken(fcmToken: String) {
        val userId = userDao.getUser()!!.id // Get user ID from local storage
        remoteDataSource.updateUserFCMToken(userId, UpdateUserFCMTokenRequest(fcmToken))
    }

    suspend fun acceptTermsAndConditions(): Result<Unit> {
        val userId = userDao.getUser()!!.id // Get user ID from local storage
        return remoteDataSource.updateTermsAndConditions(userId)
    }
}


