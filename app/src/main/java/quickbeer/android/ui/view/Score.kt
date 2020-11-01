package quickbeer.android.ui.view

import quickbeer.android.R

object Score {

    fun fromTick(score: Int?): Stars {
        return if (score == null) {
            Stars.UNRATED
        } else when (score) {
            1 -> Stars.SCORE_1
            2 -> Stars.SCORE_2
            3 -> Stars.SCORE_3
            4 -> Stars.SCORE_4
            5 -> Stars.SCORE_5
            else -> Stars.UNRATED
        }
    }

    fun fromRating(rating: Int): String {
        return if (rating >= 0) {
            rating.toString()
        } else {
            "?"
        }
    }

    enum class Stars(val resource: Int) {
        UNRATED(R.drawable.score_unrated),
        SCORE_1(R.drawable.score_1),
        SCORE_2(R.drawable.score_2),
        SCORE_3(R.drawable.score_3),
        SCORE_4(R.drawable.score_4),
        SCORE_5(R.drawable.score_5);
    }
}
