package spontaniius.data.remote.models

data class UpdateEventCardsRequest(
    val card_ids: List<Int> // List of new card IDs to merge
)
