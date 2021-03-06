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
package quickbeer.android;

public interface Constants {

    int DEFAULT_USER_ID = 0;
    int NAV_ARROW_ANIMATION_DURATION = 350;

    int REVIEWS_PER_PAGE = 10;
    int USER_REVIEWS_PER_PAGE = 10;

    String API_URL = "https://www.ratebeer.com";
    String API_KEY_NAME = "apiKey";

    String BEER_PATH = "https://ratebeer.com/beer/%d/";
    String BEER_IMAGE_PATH = "https://res.cloudinary.com/ratebeer/image/upload/w_250,c_limit/beer_%d.jpg";
    String BREWER_IMAGE_PATH = "https://res.cloudinary.com/ratebeer/image/upload/w_250,c_limit/brew_%d.jpg";
    String FLAG_IMAGE_PATH = "https://ztesch.fi/quickbeer/flags/%s.svg";
    String FACEBOOK_PATH = "https://www.facebook.com/%s";
    String TWITTER_PATH = "https://www.twitter.com/%s";
    String WIKIPEDIA_PATH = "https://en.m.wikipedia.org/wiki/%s";
    String GOOGLE_MAPS_PATH = "https://www.google.com/maps/place/%s";

    String LOGIN_DEFAULT_SAVE_INFO = "on";
    String META_QUERY_PREFIX = "__";

    String ID_KEY = "idKey";
    String PAGER_INDEX = "pagerIndex";

    interface Preferences {
        String FIRST_RUN_DRAWER = "firstRunDrawer";
    }

}
