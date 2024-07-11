package quickbeer.android.ui.actionmenu

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.annotation.StringRes

@Keep
abstract class Action(@StringRes open val text: Int) : Parcelable
