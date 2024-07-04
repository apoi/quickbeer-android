package quickbeer.android.ui.actionmenu

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Action(val key: Int, @StringRes val name: Int) : Parcelable
