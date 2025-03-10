package spontaniius.data.remote.models

data class ReportRequest(
    val userId: String,
    val cardId: Int,
    val reportText: String
)
