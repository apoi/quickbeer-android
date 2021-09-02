package quickbeer.android.util.ktx

import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog

fun AlertDialog.Builder.setPositiveAction(
    @StringRes textId: Int,
    listener: () -> Unit
): AlertDialog.Builder {
    return setPositiveButton(textId) { _, _ -> listener() }
}

fun AlertDialog.Builder.setNegativeAction(
    @StringRes textId: Int,
    listener: (DialogInterface) -> Unit
): AlertDialog.Builder {
    return setNegativeButton(textId) { dialog, _ -> listener(dialog) }
}
