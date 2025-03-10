package spontaniius.data.remote.models

import java.util.*

data class ReportResponse(
    val reportId: Int,
    val reporterId: String,
    val reportedId: String,
    val reportText: String,
    val createdAt: Date
)
