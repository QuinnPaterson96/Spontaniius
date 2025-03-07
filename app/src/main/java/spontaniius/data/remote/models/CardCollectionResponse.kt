package spontaniius.data.remote.models

import java.time.ZonedDateTime

data class CardCollectionResponse(
    val collectionId: Int,    // Unique collection ID
    val userId: String,       // User who owns this collection
    val cardIds: List<Int>?,  // List of card IDs collected (nullable)
    val eventId: Int,         // Event where cards were exchanged
    val meetingDate: ZonedDateTime // Date of meeting
)
