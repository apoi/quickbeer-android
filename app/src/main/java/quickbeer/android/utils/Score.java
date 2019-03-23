/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.utils;

import androidx.annotation.Nullable;

import quickbeer.android.R;

public class Score {

    public enum Stars {
        UNRATED(R.drawable.score_unrated),
        SCORE_1(R.drawable.score_1),
        SCORE_2(R.drawable.score_2),
        SCORE_3(R.drawable.score_3),
        SCORE_4(R.drawable.score_4),
        SCORE_5(R.drawable.score_5);

        private final int resource;

        Stars(int resource) {
            this.resource = resource;
        }

        public int getResource() {
            return resource;
        }
    }

    public static Stars fromTick(@Nullable Integer score) {
        if (score == null) {
            return Stars.UNRATED;
        }

        switch (score) {
            case 1:
                return Stars.SCORE_1;
            case 2:
                return Stars.SCORE_2;
            case 3:
                return Stars.SCORE_3;
            case 4:
                return Stars.SCORE_4;
            case 5:
                return Stars.SCORE_5;
            default:
                return Stars.UNRATED;
        }
    }

    public static String fromRating(int rating) {
        if (rating >= 0) {
            return String.valueOf(rating);
        } else {
            return "?";
        }
    }
}
