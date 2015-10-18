package quickbeer.android.next.network.utils;

import java.util.HashMap;
import java.util.Map;

import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.utils.ApiKey;

/**
 * Created by antti on 18.10.2015.
 */
public class NetworkUtils {

    private static String apiKey;

    public static Map<String, String> createRequestParams(String key, String value) {
        if (apiKey == null) {
            initApiKey();
        }

        Map<String, String> map = new HashMap<>();
        map.put("k", apiKey);
        map.put(key, value);

        return map;
    }

    private static void initApiKey() {
        apiKey = ApiKey.getApiKey(QuickBeer.getInstance().getApplicationContext());
    }
}
