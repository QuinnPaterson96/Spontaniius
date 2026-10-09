package spontaniius.ui.user_menu

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import spontaniius.data.remote.AccountDeletionIdentity
import retrofit2.HttpException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import spontaniius.data.repository.AccountDeletionCleanup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import spontaniius.data.local.dao.UserDao
import spontaniius.data.repository.UserRepository
import spontaniius.domain.models.User
import javax.inject.Inject

@HiltViewModel
class UserOptionsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val cleanup: AccountDeletionCleanup,
    private val userDao: UserDao
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _userUpdated = MutableLiveData<Boolean>()
    val userUpdated: LiveData<Boolean> = _userUpdated

    private val _accountDeleted = MutableLiveData<Boolean>()
    val accountDeleted: LiveData<Boolean> = _accountDeleted

    private var serverDeletionCompleted = false
    private var deletionIdentity: AccountDeletionIdentity? = null
    private val _deletionError = MutableLiveData<String?>()
    val deletionError: LiveData<String?> = _deletionError

    /**
     * Load user details from backend
     */
    fun loadUser() {
        _isLoading.value = true
        viewModelScope.launch {
            try{
            _user.postValue(userDao.getUser()?.toDomainModel())
            }finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update user details and send to backend
     */
    fun updateUser(name: String, phone: String, gender: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = userRepository.updateUser(name, phone, gender)
            result.onSuccess {
                _userUpdated.postValue(true)
            }
            _isLoading.value = false

        }
    }

    fun deleteUser() {
        if (_isLoading.value == true) return
        _isLoading.value = true
        cleanup.requestInProgress = true
        _deletionError.value = null
        viewModelScope.launch {
            try {
                if (!cleanup.isPending && !serverDeletionCompleted) {
                    if (deletionIdentity == null) {
                        val user = FirebaseAuth.getInstance().currentUser
                            ?: throw IllegalStateException("Sign in before deleting your account")
                        val token = user.getIdToken(false).await().token
                            ?: throw IllegalStateException("Sign-in token unavailable")
                        check(FirebaseAuth.getInstance().currentUser?.uid == user.uid)
                        deletionIdentity = AccountDeletionIdentity(user.uid, token)
                    }
                    userRepository.deleteUser(checkNotNull(deletionIdentity)).getOrThrow()
                    serverDeletionCompleted = true
                }
                // Once the server confirms deletion, rotation must not interrupt local cleanup.
                withContext(NonCancellable) {
                    if (cleanup.isPending) cleanup.resume() else cleanup.afterServerCompletion(checkNotNull(deletionIdentity).uid)
                }
                deletionIdentity = null
                _user.value = null
                _accountDeleted.value = true
            } catch (error: Exception) {
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid
                _deletionError.value = when {
                    deletionIdentity != null && currentUid != null && currentUid != deletionIdentity?.uid -> "Your sign-in changed. This request belongs to your previous account. No data from the current account was cleared. Use spontaniius.com/delete-account for assistance with the previous request."
                    cleanup.isPending || serverDeletionCompleted -> "Your server account was deleted. Device cleanup could not finish. Retry to clear cached data and notifications."
                    error is HttpException && error.code() == 409 -> "Deletion requires support review. Your account has not been fully deleted. Visit spontaniius.com/delete-account for assistance."
                    else -> "Account deletion has not completed. Please retry. Your sign-in is kept for retry. Account use is paused once deletion cleanup begins."
                }
            } finally {
                cleanup.requestInProgress = false
                _isLoading.value = false
            }
        }
    }
}
