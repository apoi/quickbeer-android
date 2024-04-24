package quickbeer.android

@Suppress("ktlint:standard:max-line-length")
object Constants {

    const val QUERY_MIN_LENGTH = 4

    // URLS

    const val API_URL =
        "https://www.ratebeer.com"

    const val API_LOGIN_PAGE =
        "Signin_r.asp"

    const val API_REVIEWS_PAGE =
        "/json/revs.asp"

    const val BEER_PATH =
        "https://ratebeer.com/beer/%d/"

    const val BEER_IMAGE_PATH =
        "https://res.cloudinary.com/ratebeer/image/upload/w_250,c_limit/beer_%d.jpg"

    const val BREWER_IMAGE_PATH =
        "https://res.cloudinary.com/ratebeer/image/upload/w_250,c_limit/brew_%d.jpg"

    const val USER_AVATAR_PATH =
        "https://res.cloudinary.com/ratebeer/image/upload/w_300,c_limit,q_100,d_user_def.png/user_%s.jpg"

    const val USER_AVATAR_HIRES_PATH =
        "http://res.cloudinary.com/ratebeer/image/upload/w_1024,c_limit,q_100,d_user_def.png/user_%s.jpg"

    const val FLAG_IMAGE_PATH =
        "https://ztesch.fi/quickbeer/flags/%s.svg"

    const val FACEBOOK_PATH =
        "https://www.facebook.com/%s"

    const val TWITTER_PATH =
        "https://www.twitter.com/%s"

    const val WIKIPEDIA_PATH =
        "https://en.m.wikipedia.org/wiki/%s"

    const val GOOGLE_MAPS_PATH =
        "https://www.google.com/maps/place/%s"

    const val PLAY_STORE =
        "https://play.google.com/store/apps/details?id=quickbeer.android"

    const val QUICKBEER_GITHUB =
        "https://github.com/apoi/quickbeer-android"

    const val QUICKBEER_LICENSE =
        "https://ztesch.fi/quickbeer/license/"

    const val OPEN_SOURCE_LICENSES =
        "https://ztesch.fi/quickbeer/open-source"

    const val GRAPHICS_LICENSES =
        "https://ztesch.fi/quickbeer/graphics-assets"

    const val PRIVACY_POLICY =
        "https://ztesch.fi/quickbeer/privacy-policy"
}
