package spontaniius.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import spontaniius.domain.models.Card

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey val cardId: Int,
    val userId: String,
    val name: String,
    val background: String,
    val greeting: String?
) {
    // ✅ Convert CardEntity to Domain Model
    fun toDomain(): Card {
        return Card(
            id = cardId,
            userId = userId,
            name = name,
            background = background,
            greeting = greeting
        )
    }

    companion object {
        // ✅ Convert Domain Model to CardEntity
        fun fromDomain(card: Card): CardEntity {
            return CardEntity(
                cardId = card.id,
                userId = card.userId,
                name = card.name,
                background = card.background,
                greeting = card.greeting
            )
        }
    }
}
