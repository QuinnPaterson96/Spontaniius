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

    @Inject lateinit var context: Context
    @Inject lateinit var userRepository: UserRepository
    @Inject lateinit var userDao: UserDao

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // If a notification payload is present, show it immediately
        remoteMessage.notification?.let {
            showNotification(it.title ?: "Spontaniius", it.body ?: "You have a new message.")
            return
        }

        // Otherwise, handle based on custom data payload
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
            retryUntilUserAvailable { sendTokenToServer(token) }
        }
    }

    private fun sendTokenToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                userRepository.updateUserFCMToken(token)
                Log.d("FCMService", "✅ FCM Token successfully updated on server")
            } catch (e: Exception) {
                Log.e("FCMService", "❌ Failed to update FCM Token", e)
            }
        }
    }

    private fun handleEventCreated(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "New Event Nearby!"
        val message = remoteMessage.notification?.body ?: remoteMessage.data["event_name"]
        ?: "An event was created near you."

        showNotification(title, message)
    }

    private fun handleUserJoined(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "New Member!"
        val message = remoteMessage.notification?.body
            ?: "${remoteMessage.data["user_name"] ?: "Someone"} just joined your event."

        showNotification(title, message)
    }

    private fun handleGenericNotification(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Notification"
        val message = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "You have a new notification."

        showNotification(title, message)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "spontaniius_notifications"
        val channel = NotificationChannel(
            channelId,
            "Spontaniius Notifications",
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        val notificationManager = NotificationManagerCompat.from(context)
        val largeIconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.spontaniius_notification)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED) {

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_monochrome)
                .setLargeIcon(largeIconBitmap)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)

            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
        } else {
            Log.w("FCMService", "❗ Notification permission not granted. Skipping.")
        }
    }

    private suspend fun retryUntilUserAvailable(action: suspend () -> Unit) {
        var retries = 0
        while (userDao.getUser() == null && retries < 5) {
            delay(1000)
            retries++
        }
        userDao.getUser()?.let { action() } ?: Log.e("FCMService", "❌ User not found after retries.")
    }
}
