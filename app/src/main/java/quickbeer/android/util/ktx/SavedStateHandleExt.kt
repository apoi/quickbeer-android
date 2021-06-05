package quickbeer.android.util.ktx

import androidx.lifecycle.SavedStateHandle
import quickbeer.android.navigation.NavParams

fun SavedStateHandle.navId(): Int {
    return get<Int>(NavParams.ID)!!
}
