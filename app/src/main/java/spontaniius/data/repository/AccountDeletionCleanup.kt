package spontaniius.data.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import spontaniius.data.local.AppDatabase
import javax.inject.Inject
import javax.inject.Singleton

/** Persist the cleanup identity and state in private app storage; never credentials. */
@Singleton
class AccountDeletionCleanup @Inject constructor(
    @ApplicationContext context: Context,
    private val database: AppDatabase,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) {
    @Volatile var requestInProgress: Boolean = false
    private val preferences = context.getSharedPreferences("account_deletion", Context.MODE_PRIVATE)
    val isPending: Boolean get() = preferences.getBoolean("local_cleanup_pending", false)

    suspend fun afterServerCompletion(expectedUid: String) = withContext(Dispatchers.IO) {
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        check(currentUid == null || currentUid == expectedUid) { "Account identity changed" }
        val cachedUid = userRepository.getUserDetails()?.external_id
        check(cachedUid == null || cachedUid == expectedUid) { "Cached account identity changed" }
        check(preferences.edit().putString("cleanup_uid", expectedUid)
            .putBoolean("local_cleanup_pending", true).commit())
        resume()
    }

    suspend fun resume() = withContext(Dispatchers.IO) {
        if (!isPending) return@withContext
        val expectedUid = preferences.getString("cleanup_uid", null)
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid
        check(currentUid == null || currentUid == expectedUid) { "Account identity changed" }
        val cachedUid = userRepository.getUserDetails()?.external_id
        check(cachedUid == null || cachedUid == expectedUid) { "Cached account identity changed" }
        // Includes cached cards and events as well as the profile; no other app DB is used by DI.
        withContext(Dispatchers.Main.immediate) {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            check(uid == null || uid == expectedUid) { "Account identity changed" }
            authRepository.signOut()
        }
        userRepository.clearUser()
        database.clearAllTables()
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
        // Deleting the registration token removes its server-side topic subscriptions.
        FirebaseMessaging.getInstance().deleteToken().await()
        check(preferences.edit().remove("local_cleanup_pending").remove("cleanup_uid").commit())
    }
}
