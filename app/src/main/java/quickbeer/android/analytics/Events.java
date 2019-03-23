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
package quickbeer.android.analytics;

import androidx.annotation.NonNull;

@SuppressWarnings("MarkerInterface")
public interface Events {

    enum Screen {
        HOME_BEERS("screen_home_beers"),
        HOME_BREWERS("screen_home_brewers"),
        PROFILE_LOGIN("screen_profile_login"),
        PROFILE_VIEW("screen_profile_view"),
        ABOUT("screen_about"),
        BEER_SEARCH("screen_beer_search"),
        BEER_DETAILS("screen_beer_details"),
        BEER_REVIEWS("screen_beer_details"),
        BREWER_DETAILS("screen_brewer_details"),
        BREWER_BEERS("screen_brewer_beers"),
        STYLE_DETAILS("screen_style_details"),
        STYLE_BEERS("screen_style_beers"),
        COUNTRY_DETAILS("screen_country_details"),
        COUNTRY_BEERS("screen_country_beers"),
        BARCODE_SCANNER("screen_barcode_scanner"),
        TOP_BEERS("screen_top_beers"),
        COUNTRY_LIST("screen_country_list"),
        COUNTRY("screen_country_best"),
        STYLE_LIST("screen_style_list"),
        STYLE("screen_style_best");

        private final String value;

        Screen(@NonNull String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    enum Entry {
        LINK_BEER("entry_beer_link"),
        LINK_BREWER("entry_brewer_link"),
        LINK_STYLE("entry_style_link");

        private final String value;

        Entry(@NonNull String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    enum Action {
        TICK_ADD("action_tick_add"),
        TICK_REMOVE("action_tick_remove");

        private final String value;

        Action(@NonNull String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    enum LaunchAction {
        BREWER_WEBSITE("launch_brewer_website"),
        BREWER_FACEBOOK("launch_brewer_facebook"),
        BREWER_TWITTER("launch_brewer_twitter"),

        ABOUT_GOOGLE_PLAY("launch_about_google_play"),
        ABOUT_SPICE_PROGRAM("launch_about_spice"),
        ABOUT_IIRO("launch_about_iiro"),
        ABOUT_SOURCE("launch_about_source"),
        ABOUT_LICENSE("launch_about_license"),
        ABOUT_OPEN_SOURCE("launch_about_open_source"),
        ABOUT_GRAPHICS_ASSETS("launch_about_graphics_assets"),
        ABOUT_PRIVACY_POLICY("launch_about_privacy_policy");

        private final String value;

        LaunchAction(@NonNull String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

}
