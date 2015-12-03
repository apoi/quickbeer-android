package quickbeer.android.next.data.store;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.data.schematicprovider.BeerColumns;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.pojo.Beer;

/**
 * Created by antti on 17.10.2015.
 */
public class BeerStore extends StoreBase<Beer, Integer> {
    private static final String TAG = BeerStore.class.getSimpleName();

    public BeerStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);

        QuickBeer.getInstance().getGraph().inject(this);
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
        contentValues.put(BeerColumns.JSON, getGson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected Beer read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(BeerColumns.JSON));
        return getGson().fromJson(json, Beer.class);
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

    @NonNull
    @Override
    protected ContentValues mergeValues(ContentValues v1, ContentValues v2) {
        Beer b1 = getGson().fromJson(v1.getAsString(BeerColumns.JSON), Beer.class);
        Beer b2 = getGson().fromJson(v2.getAsString(BeerColumns.JSON), Beer.class);
        return getContentValuesForItem(b1.overwrite(b2));
    }
}
