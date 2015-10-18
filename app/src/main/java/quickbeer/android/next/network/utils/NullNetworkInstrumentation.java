package quickbeer.android.next.network.utils;

import android.support.annotation.NonNull;

import com.squareup.okhttp.OkHttpClient;

/**
 * Created by Pawel Polanski on 7/18/15.
 */
public class NullNetworkInstrumentation implements NetworkInstrumentation<OkHttpClient> {

    @NonNull
    @Override
    public OkHttpClient decorateNetwork(@NonNull OkHttpClient httpClient) {
        return httpClient;
    }

    @Override
    public void init() { }
}
