package quickbeer.android.next.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import quickbeer.android.next.R;

/**
 * Created by antti on 17.10.2015.
 */
public class ApiKey {
    private static final String TAG = ApiKey.class.getSimpleName();

    public static String getApiKey(@NonNull Context context) {
        try {
            // RateBeer API keys may not be shared. You'll need to acquire your own key.
            // Store the key as plain text in the file app/src/main/res/raw/apikey.txt.
            InputStream stream = context.getResources().openRawResource(R.raw.apikey);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String value = reader.readLine();
            if (value == null || value.trim().isEmpty()) {
                Log.e(TAG, "Invalid API key!");
            }

            return value;
        } catch (IOException e) {
            return null;
        }
    }
}