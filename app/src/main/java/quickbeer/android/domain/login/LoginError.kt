package quickbeer.android.domain.login

sealed class LoginError : Throwable() {
    data object InvalidCredentials : LoginError()
    data object ConnectionError : LoginError()
    data object UnknownError : LoginError()
}
