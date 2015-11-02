package quickbeer.android.next.utils;

import quickbeer.android.next.R;

/**
 * Created by antti on 2.11.2015.
 */
public class Score {

    public enum Stars {
        UNRATED(R.drawable.score_unrated),
        SCORE_1(R.drawable.score_1),
        SCORE_2(R.drawable.score_2),
        SCORE_3(R.drawable.score_3),
        SCORE_4(R.drawable.score_4),
        SCORE_5(R.drawable.score_5);

        private int resource;

        Stars(int resource) {
            this.resource = resource;
        }

        public int getResource() {
            return resource;
        }
    }
}
