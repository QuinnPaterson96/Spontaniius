package spontaniius.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import com.spontaniius.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import spontaniius.data.local.dao.UserDao
import spontaniius.data.repository.UserRepository
import javax.inject.Inject

@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {

    @Inject
    lateinit var context: Context // ✅ Injected context using Hilt

    @Inject
    lateinit var userRepository: UserRepository // ✅ Inject user repository to update the token

    @Inject
    lateinit var userDao: UserDao

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val type = remoteMessage.data["type"]

        when (type) {
            "EVENT_CREATED" -> handleEventCreated(remoteMessage)
            "USER_JOINED_EVENT" -> handleUserJoined(remoteMessage)
            else -> handleGenericNotification(remoteMessage)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService", "New FCM Token: $token")

        CoroutineScope(Dispatchers.IO).launch {
            retryUntilUserAvailable {  sendTokenToServer(token) }
        }
    }

    private fun sendTokenToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userRepository.updateUserFCMToken(token)
                Log.d("FCMService", "FCM Token successfully updated on server")
            } catch (e: Exception) {
                Log.e("FCMService", "Failed to update FCM Token", e)
            }
        }
    }

    private fun handleEventCreated(remoteMessage: RemoteMessage) {
        val title = "New Event Nearby!"
        val message = remoteMessage.data["event_name"] ?: "An event was created near you."

        showNotification(title, message)
    }

    private fun handleUserJoined(remoteMessage: RemoteMessage) {
        val title = "New Member!"
        val message = "${remoteMessage.data["user_name"]} just joined your event."

        showNotification(title, message)
    }

    private fun handleGenericNotification(remoteMessage: RemoteMessage) {

        Log.w("FCM Service", remoteMessage.toString())
        val title = remoteMessage.data["title"] ?: "Notification"
        val message = remoteMessage.data["body"] ?: "You have a new notification."

        showNotification(title, message)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "spontaniius_notifications"

        val channel = NotificationChannel(
            channelId, "Spontaniius Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notificationManager = NotificationManagerCompat.from(context)
        val largeIconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.spontaniius_notification)

        // ✅ Check if permission is granted before sending notification
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_monochrome)
                .setLargeIcon(largeIconBitmap)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            notificationManager.notify(1, builder.build())
        } else {
            Log.w("FCMService", "Notification permission not granted. Skipping notification.")
        }
    }

    suspend fun retryUntilUserAvailable(action: suspend () -> Unit) {
        var retries = 0
        while (userDao.getUser() == null && retries < 5) {
            delay(1000) // Wait 1 second before retrying
            retries++
        }
        if (userDao.getUser() != null) {
            action()
        } else {
            Log.e("FCMService", "User still not available after retries. Skipping FCM token update.")
        }
    }

}