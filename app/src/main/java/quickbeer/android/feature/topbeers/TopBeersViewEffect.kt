package quickbeer.android.feature.topbeers

sealed class TopBeersViewEffect {
    data class Search(val query: String) : TopBeersViewEffect()
}
