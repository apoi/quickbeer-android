package quickbeer.android.feature.login

sealed class LoginError : Throwable() {
    object InvalidCredentials : LoginError()
    object ConnectionError : LoginError()
    object UnknownError : LoginError()
}
