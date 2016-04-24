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
package quickbeer.android.next.utils;

import quickbeer.android.next.R;

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
}
