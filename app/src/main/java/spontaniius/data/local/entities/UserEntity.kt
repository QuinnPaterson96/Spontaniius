package spontaniius.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: String, // UUID
    val name: String,
    val phone: String,
    val gender: String?,
    val card_id: Int?,
    val external_id: String?,
    val auth_provider: String?
)
