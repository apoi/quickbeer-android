package quickbeer.android.analytics;

import android.support.annotation.NonNull;

@SuppressWarnings("MarkerInterface")
public interface Events {

    enum Screen {
        HOME_BEERS("Home"),
        HOME_BREWERS("Home"),
        PROFILE_LOGIN("ProfileLogin"),
        PROFILE_VIEW("ProfileView"),
        ABOUT("About"),
        BEER_SEARCH("BeerSearch"),
        BEER_DETAILS("BeerDetails"),
        BEER_REVIEWS("BeerDetails"),
        BREWER_DETAILS("BrewerDetails"),
        BREWER_BEERS("BrewerBeers"),
        BARCODE_SCANNER("BarcodeScanner"),
        TOP_BEERS("TopBeers"),
        COUNTRY_LIST("CountryList"),
        COUNTRY("CountryBest"),
        STYLE_LIST("StyleList"),
        STYLE("StyleBest");

        private final String value;

        Screen(@NonNull String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    enum Action {
        TICK_ADD("TickAdd"),
        TICK_REMOVE("TickRemove");

        private final String value;

        Action(@NonNull String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    enum LaunchAction {
        BREWER_WEBSITE("BrewerWebsite"),
        BREWER_FACEBOOK("BrewerFacebook"),
        BREWER_TWITTER("BrewerTwitter"),

        ABOUT_SOURCE("AboutSource"),
        ABOUT_LICENSE("AboutLicense"),
        ABOUT_OPEN_SOURCE("AboutOpenSource"),
        ABOUT_GRAPHICS_ASSETS("AboutGraphicsAssets"),
        ABOUT_PRIVACY_POLICY("AboutPrivacyPolicy");

        private final String value;

        LaunchAction(@NonNull String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

}
