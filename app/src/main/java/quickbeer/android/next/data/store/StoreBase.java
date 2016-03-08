package quickbeer.android.next.data.store;

import android.content.ContentResolver;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import javax.inject.Inject;

import io.reark.reark.data.store.SingleItemContentProviderStore;

public abstract class StoreBase<T, U> extends SingleItemContentProviderStore<T, U> {

    @Inject
    Gson gson;

    public StoreBase(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    protected Gson getGson() {
        return gson;
    }
}
