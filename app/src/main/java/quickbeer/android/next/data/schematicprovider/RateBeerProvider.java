package quickbeer.android.next.data.schematicprovider;

import android.net.Uri;
import android.support.annotation.NonNull;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

import quickbeer.android.next.utils.Preconditions;

@ContentProvider(authority = quickbeer.android.next.data.schematicprovider.RateBeerProvider.AUTHORITY, database = RateBeerDatabase.class)
public class RateBeerProvider {
    public static final String AUTHORITY = "android.quickbeer.next.data.schematicProvider.RateBeerProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static Uri buildUri(@NonNull String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        Uri uri = builder.build();
        Preconditions.checkNotNull(uri, "Uri cannot be null.");
        return uri;
    }

    @TableEndpoint(table = RateBeerDatabase.USER_SETTINGS) public static class UserSettings {
        @ContentUri(
                path = RateBeerDatabase.USER_SETTINGS,
                type = "vnd.android.cursor.dir/vnd.android.quickbeer.next.usersettings",
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri USER_SETTINGS = Uri.parse("content://" + AUTHORITY + "/" + RateBeerDatabase.USER_SETTINGS);

        @InexactContentUri(
                path = RateBeerDatabase.USER_SETTINGS + "/#",
                name = "USER_SETTINGS_ID",
                type = "vnd.android.cursor.item/vnd.android.quickbeer.next.usersettings",
                whereColumn = UserSettingsColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(RateBeerDatabase.USER_SETTINGS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = RateBeerDatabase.NETWORK_REQUEST_STATUSES) public static class NetworkRequestStatuses {
        @ContentUri(
                path = RateBeerDatabase.NETWORK_REQUEST_STATUSES,
                type = "vnd.android.cursor.dir/vnd.android.quickbeer.next.networkrequeststatus",
                defaultSort = JsonIdColumns.ID + " ASC")
        public static final Uri NETWORK_REQUEST_STATUSES = Uri.parse("content://" + AUTHORITY + "/" + RateBeerDatabase.NETWORK_REQUEST_STATUSES);

        @InexactContentUri(
                path = RateBeerDatabase.NETWORK_REQUEST_STATUSES + "/*",
                name = "NETWORK_REQUEST_STATUSES_ID",
                type = "vnd.android.cursor.item/vnd.android.quickbeer.next.networkrequeststatus",
                whereColumn = NetworkRequestStatusColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(RateBeerDatabase.NETWORK_REQUEST_STATUSES, String.valueOf(id));
        }
    }
}
