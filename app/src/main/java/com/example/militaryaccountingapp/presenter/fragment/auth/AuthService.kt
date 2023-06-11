package com.example.militaryaccountingapp.presenter.fragment.auth

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import androidx.core.app.ActivityCompat
import com.example.militaryaccountingapp.R
import com.example.militaryaccountingapp.domain.helper.Results
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import timber.log.Timber

object AuthService {

    var callbackManager: CallbackManager? = null
        private set

    suspend fun signInWithFacebook(
        activity: Activity,
        onResultListener: (results: Results<LoginResult>) -> Unit
    ) {
        callbackManager = CallbackManager.Factory.create()

        /* implementation on standard widget
        loginButton = findViewById(R.id.login_button)
        loginButton.setReadPermissions(listOf("email", "public_profile"))
        loginButton.registerCallback(
            callbackManager, object : FacebookCallback<LoginResult> { ...
         */

        createFacebookManager(activity, onResultListener)
        /* { loginResult ->
            val res = resultWrapper(
                signInFacebookUseCase(loginResult.accessToken.token)
            ) {
                Result.Success(true)
            }
            _data.update { it.copy(isSigned = res) }
        }*/
    }

    private suspend fun createFacebookManager(
        activity: Activity,
        onResultListener: (results: Results<LoginResult>) -> Unit
    ) = LoginManager.getInstance().apply {
        logInWithReadPermissions(
            activity, listOf("email", "public_profile")
        )

        registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    onResultListener(Results.Success(result))
                }

                override fun onError(error: FacebookException) {
                    onResultListener(
                        logError(
                            "Result.Failure FACEBOOK - ${error.message}",
                            error, Results.FailureType.FACEBOOK
                        )
                    )
                }

                override fun onCancel() {
                    onResultListener(
                        Results.Canceled(Exception("FacebookCallback: Request canceled"))
                    )
                }
            }
        )
    }

//    ====== Google =====

    const val REQ_ONE_TAP = 12345

    private fun createGoogleSignInRequest(
        webClientId: String,
        onlyAuthorizedAccounts: Boolean
    ): BeginSignInRequest = BeginSignInRequest.builder()
        .setPasswordRequestOptions(
            BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build()
        )
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                // Your server's client ID, not your Android client ID.
                .setServerClientId(webClientId)
                // Only show accounts previously used to sign in.
                .setFilterByAuthorizedAccounts(onlyAuthorizedAccounts)
                .build()
        )
        .build()


    private lateinit var oneTapClient: SignInClient

    private var blockOneTapUI: Boolean = false

    fun handleGoogleAccessToken(
        data: Intent?,
        onSuccessListener: (idToken: String) -> Unit
    ): Results<Boolean> {
        var res: Results<Boolean> = Results.Loading(false)

        try {
            oneTapClient
                .getSignInCredentialFromIntent(data)
                .googleIdToken?.let { it ->
                    onSuccessListener(it)
                }
        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Timber.e("One-tap dialog was closed.")
                    res = Results.Canceled(Exception("One-tap dialog was closed."))
//                    _toast.value = activity.getString(R.string.request_canceled)
                    // Don't re-prompt the user.
//                    showOneTapUI = false
                }

                CommonStatusCodes.NETWORK_ERROR -> {
                    Timber.e("One-tap encountered a network error.")
                    res = Results.Failure(
                        Exception("One-tap encountered a network error.", e),
                        Results.FailureType.NETWORK
                    )
                    // Try again or just ignore.
                }

                else -> {
                    res = Results.Failure(
                        Exception("Couldn't get credential from result. (${e.localizedMessage})"),
                        Results.FailureType.GOOGLE
                    )
                }
            }
        } catch (e: Exception) {
            res = Results.Failure(e, Results.FailureType.GOOGLE)
        } finally {
            blockOneTapUI = false
        }
        return res
    }


    fun signInWithGoogle(
        activity: Activity,
        isLogin: Boolean = false,
        onResultListener: (Results<Boolean>) -> Unit
    ) {
        // if one tap blocked the user is already trying to log in
        if (blockOneTapUI) return

        // block the one tap while google sign in process is not over
        blockOneTapUI = true

        oneTapClient = Identity.getSignInClient(activity)

        val signUpRequest = createGoogleSignInRequest(
            activity.getString(R.string.default_web_client_id), isLogin
        )

        // If the user hasn't already declined to use One Tap sign-in
        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener(activity) { result ->
                try {
                    ActivityCompat.startIntentSenderForResult(
                        activity,
                        result.pendingIntent.intentSender,
                        REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )
                } catch (e: IntentSender.SendIntentException) {
                    blockOneTapUI = false
                    onResultListener(
                        logError(
                            "Couldn't start One Tap UI: " + e.localizedMessage,
                            e, Results.FailureType.GOOGLE
                        )
                    )
                }
            }.addOnFailureListener(activity) {
                it.localizedMessage?.let { e ->
                    blockOneTapUI = false
                    // No saved credentials found. Show error
                    onResultListener(
                        logError(
                            "No saved credentials found. Show error: $e",
                            it, Results.FailureType.GOOGLE
                        )
                    )
                }
            }
    }

    private fun <T> logError(
        message: String, e: Exception,
        type: Results.FailureType
    ): Results.Failure<T> {
        Timber.e(message)
        return Results.Failure(Exception(message, e), type)
    }
}
