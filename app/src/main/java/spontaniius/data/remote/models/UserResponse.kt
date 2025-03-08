package spontaniius.data.remote.models

import spontaniius.domain.models.User

data class UserResponse(
    val id: String,
    val name: String,
    val phone: String?,
    val gender: String?,
    val card_id: Int?,
    val external_id: String?,
    val auth_provider: String?
){
    fun toDomainModel(): User {
        return User(id, name, phone, gender, card_id, external_id, auth_provider)
    }

    // Convert from User (Domain) to API request body
    companion object {
        fun fromDomainModel(user: User): UserResponse {
            return UserResponse(user.id, user.name, user.phone, user.gender, user.cardId, user.externalId, user.authProvider)
        }
    }
}
