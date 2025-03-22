package spontaniius.data.remote.models

import com.google.gson.annotations.SerializedName
import spontaniius.data.local.entities.CardEntity
import spontaniius.domain.models.Card

data class CardResponse(
    @SerializedName("card_id") val cardId: Int,  // ✅ Matches JSON
    @SerializedName("user_id") val userId: String,
    @SerializedName("card_text") val name: String,  // ✅ Matches JSON ("card_text" instead of "name")
    @SerializedName("background") val background: String,  // ✅ Changed type from `Int?` to `String?`
    @SerializedName("background_address") val backgroundAddress: String?,  // ✅ Added missing field
    @SerializedName("greeting") val greeting: String?  // ✅ Added missing field
){
    // ✅ Converts DTO → Domain Model
    fun toDomain(): Card {
        return Card(
            id = this.cardId,
            userId = this.userId,
            name = this.name,
            background = this.background,
            greeting = this.greeting
        )
    }

    fun toEntity(): CardEntity{
        return CardEntity(
            cardId = this.cardId,
            userId = this.userId,
            name = this.name,
            background = this.background,
            greeting = this.greeting
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
                greeting = card.greeting
            )
        }
    }

}