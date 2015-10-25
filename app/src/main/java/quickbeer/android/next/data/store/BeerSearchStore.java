package quickbeer.android.next.data.store;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import quickbeer.android.next.data.base.store.SingleItemContentProviderStoreBase;
import quickbeer.android.next.data.schematicprovider.BeerSearchColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.pojo.BeerSearch;
import quickbeer.android.next.utils.Preconditions;

/**
 * Created by antti on 17.10.2015.
 */
public class BeerSearchStore extends SingleItemContentProviderStoreBase<BeerSearch, String> {
    private static final String TAG = BeerSearchStore.class.getSimpleName();

    public BeerSearchStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    @Override
    protected String getIdFor(@NonNull BeerSearch item) {
        Preconditions.checkNotNull(item, "Beer Search cannot be null.");

        return item.getSearch();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return RateBeerProvider.BeerSearches.BEER_SEARCHES;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { BeerSearchColumns.SEARCH, BeerSearchColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(BeerSearch item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BeerSearchColumns.SEARCH, item.getSearch());
        contentValues.put(BeerSearchColumns.JSON, new Gson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected BeerSearch read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BeerSearchColumns.JSON));
        final BeerSearch value = new Gson().fromJson(json, BeerSearch.class);
        return value;
    }

    @NonNull
    @Override
    public Uri getUriForKey(@NonNull String id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.BeerSearches.withSearch(id);
    }
}