package spontaniius.data.remote.models

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AccountDeletionResponseTest {
    @Test fun onlyExplicit200CompletionPermitsCleanup() {
        assertTrue(AccountDeletionResponse("completed").confirmsCompletion(200))
        for (code in listOf(202, 204, 409, 503)) {
            assertFalse(AccountDeletionResponse("completed").confirmsCompletion(code))
        }
    }

    @Test fun pendingMissingAndUnknownStatusNeverPermitCleanup() {
        for (status in listOf(null, "pending", "pending_review", "accepted", "Completed")) {
            assertFalse(AccountDeletionResponse(status).confirmsCompletion(200))
        }
    }
}
