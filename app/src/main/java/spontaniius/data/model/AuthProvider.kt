package spontaniius.data.model

enum class AuthProvider(val providerName: String) {
    GOOGLE("Google"),
    FIREBASE("Firebase"),
    FACEBOOK("Facebook"),
    APPLE("Apple"),
    EMAIL("Email");

    companion object {
        fun fromString(value: String): AuthProvider? {
            return values().find { it.providerName.equals(value, ignoreCase = true) }
        }
    }
}
