package quickbeer.android.util.ktx

import androidx.lifecycle.SavedStateHandle
import quickbeer.android.domain.user.User
import quickbeer.android.navigation.NavParams

fun SavedStateHandle.navId(): Int {
    return get<Int>(NavParams.ID)!!
}

fun SavedStateHandle.user(): User {
    return get<User>(NavParams.USER)!!
}

fun SavedStateHandle.mode(): Int {
    return get<Int>(NavParams.MODE)!!
}