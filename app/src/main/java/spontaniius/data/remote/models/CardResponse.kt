package spontaniius.data.remote.models

import com.google.gson.annotations.SerializedName
import spontaniius.data.local.entities.CardEntity
import spontaniius.domain.models.Card

data class CardResponse(
    @SerializedName("card_id") val cardId: Int,  // ✅ Matches JSON
    @SerializedName("user_id") val userId: String,
    @SerializedName("user_name") val name: String,
    @SerializedName("card_text") val card_text: String?,  // ✅ Matches JSON ("card_text" instead of "name")
    @SerializedName("background") val background: String,  // ✅ Changed type from `Int?` to `String?`
    @SerializedName("background_address") val backgroundAddress: String?,  // ✅ Added missing field
){
    // ✅ Converts DTO → Domain Model
    fun toDomain(): Card {
        return Card(
            id = this.cardId,
            userId = this.userId,
            name = this.name,
            background = this.background,
            greeting = this.card_text
        )
    }

    fun toEntity(): CardEntity{
        return CardEntity(
            cardId = this.cardId,
            userId = this.userId,
            name = this.name,
            background = this.background,
            greeting = this.card_text
        )
    }

    // ✅ Converts Domain Model → DTO
    companion object {
        fun fromDomain(card: Card): CardResponse {
            return CardResponse(
                cardId = card.id,
                userId = card.userId,
                name = card.name,
                background = card.background,
                backgroundAddress = card.background,
                card_text = card.greeting
            )
        }
    }

}