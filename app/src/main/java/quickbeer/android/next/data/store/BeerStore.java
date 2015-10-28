package quickbeer.android.next.data.store;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import io.reark.reark.data.store.SingleItemContentProviderStore;
import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.data.schematicprovider.BeerColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.pojo.Beer;

/**
 * Created by antti on 17.10.2015.
 */
public class BeerStore extends SingleItemContentProviderStore<Beer, Integer> {
    private static final String TAG = BeerStore.class.getSimpleName();

    public BeerStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull Beer item) {
        Preconditions.checkNotNull(item, "Beer cannot be null.");

        return item.getId();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return RateBeerProvider.Beers.BEERS;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { BeerColumns.ID, BeerColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(Beer item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(BeerColumns.ID, item.getId());
        contentValues.put(BeerColumns.JSON, new Gson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected Beer read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BeerColumns.JSON));
        return new Gson().fromJson(json, Beer.class);
    }

    @NonNull
    @Override
    protected ContentValues readRaw(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BeerColumns.JSON));
        ContentValues contentValues = new ContentValues();
        contentValues.put(BeerColumns.JSON, json);
        return contentValues;
    }

    @NonNull
    @Override
    public Uri getUriForKey(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.Beers.withId(id);
    }

    @Override
    protected boolean contentValuesEqual(ContentValues v1, ContentValues v2) {
        return v1.getAsString(BeerColumns.JSON).equals(v2.getAsString(BeerColumns.JSON));
    }
}
