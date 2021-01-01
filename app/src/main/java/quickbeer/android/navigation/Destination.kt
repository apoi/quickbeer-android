package quickbeer.android.navigation

import android.net.Uri

/**
 * Deeplink destinations reachable from anywhere.
 */
sealed class Destination(val uri: Uri) {
    class Beer(id: Int) : Destination(Uri.parse("quickbeer://beer/$id"))
    class Brewer(id: Int) : Destination(Uri.parse("quickbeer://brewer/$id"))
    class Style(id: Int) : Destination(Uri.parse("quickbeer://style/$id"))
    class Country(id: Int) : Destination(Uri.parse("quickbeer://country/$id"))
}
