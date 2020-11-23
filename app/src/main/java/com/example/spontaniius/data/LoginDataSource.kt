package com.example.spontaniius.data

import android.content.Intent
import android.util.Log
import com.amplifyframework.core.Amplify
import com.example.spontaniius.data.model.LoggedInUser
import com.example.spontaniius.ui.sign_up.PHONE_NUMBER
import com.example.spontaniius.ui.sign_up.SignUpActivity2
import com.example.spontaniius.ui.sign_up.USER_ID
import com.example.spontaniius.ui.sign_up.USER_NAME
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}