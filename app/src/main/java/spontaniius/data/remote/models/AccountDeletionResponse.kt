package spontaniius.data.remote.models

/** A successful HTTP response alone is not confirmation of completed deletion. */
data class AccountDeletionResponse(val status: String? = null) {
    fun confirmsCompletion(httpStatus: Int): Boolean = httpStatus == 200 && status == "completed"
}
