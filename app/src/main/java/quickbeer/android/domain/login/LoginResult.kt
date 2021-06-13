package quickbeer.android.domain.login

sealed class LoginResult {
    data class Success(val id: Int) : LoginResult()
    data class Error(val cause: Throwable) : LoginResult()
}
