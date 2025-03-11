package spontaniius.ui.event_chat

import java.util.*

data class ChatMessage(
    var messageText: String? = "",
    var messageUser: String? = "",
    var messageTime: Long = System.currentTimeMillis() // Default timestamp
)