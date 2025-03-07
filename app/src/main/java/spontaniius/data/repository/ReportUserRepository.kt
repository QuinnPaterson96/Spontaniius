package spontaniius.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import spontaniius.data.remote.RemoteDataSource
import spontaniius.data.remote.models.ReportRequest
import spontaniius.data.remote.models.ReportResponse
import javax.inject.Inject

class ReportUserRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {
    suspend fun reportUser(userId: String, cardId: Int, reportText: String): Result<ReportResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = ReportRequest(
                    userId = userId,
                    cardId = cardId,
                    reportText = reportText
                )

                val response = remoteDataSource.reportUser(request)
                response.map { reportResponse ->
                    Log.d("ReportUserRepository", "Report submitted successfully: ${reportResponse.reportId}")
                    reportResponse // ✅ Returns ReportResponse
                }
            } catch (e: Exception) {
                Log.e("ReportUserRepository", "Error submitting report: ${e.localizedMessage}")
                Result.failure(e)
            }
        }
    }
}
