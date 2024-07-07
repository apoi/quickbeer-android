package quickbeer.android.ui.actionmenu

import android.os.Parcelable
import androidx.annotation.StringRes

abstract class Action(@StringRes open val text: Int) : Parcelable
