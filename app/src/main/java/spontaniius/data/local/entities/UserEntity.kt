package spontaniius.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import spontaniius.domain.models.User

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String?,
    val gender: String?,
    val card_id: Int?,
    val external_id: String?,
    val auth_provider: String?
) {
    // Convert from UserEntity to User (for UI)
    fun toDomainModel(): User {
        return User(id, name, phone, gender, card_id, external_id, auth_provider)
    }

    // Convert from User (Domain) to UserEntity (for Room storage)
    companion object {
        fun fromDomainModel(user: User): UserEntity {
            return UserEntity(user.id, user.name, user.phone, user.gender, user.cardId, user.externalId, user.authProvider)
        }
    }
}
