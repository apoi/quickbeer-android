package quickbeer.android.domain.login

sealed class LoginError : Throwable() {
    object InvalidCredentials : LoginError()
    object ConnectionError : LoginError()
    object UnknownError : LoginError()
}
