package spontaniius.data.remote.models

import spontaniius.data.local.entities.CardEntity
import spontaniius.domain.models.Card

data class CardResponse(
    val cardId: Int,         // Unique card identifier
    val userId: String,      // Owner of the card
    val name: String,        // Cardholder's name
    val phone: String,       // Cardholder's phone number
    val background: Int?,    // Background ID (optional)
    val greeting: String?    // Custom greeting (optional)
){
    // ✅ Converts DTO → Domain Model
    fun toDomain(): Card {
        return Card(
            id = this.cardId,
            userId = this.userId,
            name = this.name,
            phone = this.phone,
            background = this.background,
            greeting = this.greeting
        )
    }

    fun toEntity(): CardEntity{
        return CardEntity(
            cardId = this.cardId,
            userId = this.userId,
            name = this.name,
            phone = this.phone,
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
                phone = card.phone,
                background = card.background,
                greeting = card.greeting
            )
        }
    }

}