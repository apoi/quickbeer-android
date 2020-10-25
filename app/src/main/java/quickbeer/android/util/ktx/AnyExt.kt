package quickbeer.android.util.ktx

inline fun <T> T?.ifNull(block: () -> Unit): T? {
    if (this == null) block()
    return this
}
