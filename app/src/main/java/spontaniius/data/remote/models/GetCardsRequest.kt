package spontaniius.data.remote.models

import com.google.gson.annotations.SerializedName

data class GetCardsRequest(
    @SerializedName("card_ids") val cardIds: List<Int>
)
