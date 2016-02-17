package quickbeer.android.next.data.schematicprovider;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = RateBeerDatabase.VERSION)
public final class RateBeerDatabase {
    public static final int VERSION = 1;

    @Table(NetworkRequestStatusColumns.class) public static final String NETWORK_REQUEST_STATUSES = "networkRequestStatuses";
    @Table(UserSettingsColumns.class) public static final String USER_SETTINGS = "userSettings";
    @Table(BeerColumns.class) public static final String BEERS = "beers";
    @Table(BeerSearchColumns.class) public static final String BEER_SEARCHES = "beerSearches";
    @Table(ReviewColumns.class) public static final String REVIEWS = "reviews";
}
