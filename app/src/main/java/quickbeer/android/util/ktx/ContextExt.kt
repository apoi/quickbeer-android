package quickbeer.android.util.ktx

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import quickbeer.android.Constants
import quickbeer.android.feature.beerdetails.model.Address

fun Context.getThemeColor(@AttrRes colorId: Int): Int {
    val attrs = theme.obtainStyledAttributes(intArrayOf(colorId))
    val result = attrs.getColor(0, 0)
    attrs.recycle()
    return result
}

fun Context.hideKeyboard(view: View) {
    ContextCompat.getSystemService(this, InputMethodManager::class.java)
        ?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.openUri(uri: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    startActivity(intent)
}

fun Context.openWikipedia(article: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WIKIPEDIA_PATH.format(article)))
    startActivity(intent)
}

fun Context.openMaps(address: Address) {
    if (address.address == null) return

    val street = if (address.address.contains(",")) {
        address.address.split(",")[0]
    } else {
        address.address
    }

    val link = "%s, %s, %s".format(street, address.city, address.country)
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.GOOGLE_MAPS_PATH.format(link)))
    startActivity(intent)
}
