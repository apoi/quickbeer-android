package quickbeer.android.next.network.fetchers.base;

import android.content.Intent;
import android.net.Uri;

/**
 * Created by ttuo on 16/04/15.
 */
public interface Fetcher {
    void fetch(Intent intent);
    Uri getContentUri();
}
