package spontaniius.data.remote.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class ReportResponse(
    @SerializedName("report_id") val reportId: Int,
    @SerializedName("reporter_id") val reporterId: String,
    @SerializedName("reported_id") val reportedId: String,
    @SerializedName("report_text") val reportText: String,
    @SerializedName("created_at") val createdAt: Date
)