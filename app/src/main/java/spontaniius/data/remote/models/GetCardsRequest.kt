package spontaniius.data.remote.models

data class GetCardsRequest(
    val cardIds: List<Int> // List of card IDs to fetch details for
)
