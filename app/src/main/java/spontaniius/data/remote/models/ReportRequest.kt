package spontaniius.data.remote.models

import com.google.gson.annotations.SerializedName

data class ReportRequest(
    @SerializedName("card_id") val cardId: Int,
    @SerializedName("report_text") val reportText: String,
    @SerializedName("user_id") val userId: String
)