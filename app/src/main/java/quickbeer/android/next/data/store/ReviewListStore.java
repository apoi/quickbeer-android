package quickbeer.android.next.data.store;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.data.schematicprovider.RateBeerProvider;
import quickbeer.android.next.data.schematicprovider.ReviewListColumns;
import quickbeer.android.next.pojo.ReviewList;

public class ReviewListStore extends StoreBase<ReviewList, Integer> {
    private static final String TAG = ReviewListStore.class.getSimpleName();

    public ReviewListStore(@NonNull ContentResolver contentResolver) {
        super(contentResolver);

        QuickBeer.getInstance().getGraph().inject(this);
    }

    @NonNull
    @Override
    protected Integer getIdFor(@NonNull ReviewList item) {
        Preconditions.checkNotNull(item, "ReviewList cannot be null.");

        return item.getBeerId();
    }

    @NonNull
    @Override
    public Uri getContentUri() {
        return RateBeerProvider.ReviewLists.REVIEW_LISTS;
    }

    @NonNull
    @Override
    protected String[] getProjection() {
        return new String[] { ReviewListColumns.BEER_ID, ReviewListColumns.JSON };
    }

    @NonNull
    @Override
    protected ContentValues getContentValuesForItem(ReviewList item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReviewListColumns.BEER_ID, item.getBeerId());
        contentValues.put(ReviewListColumns.JSON, getGson().toJson(item));
        return contentValues;
    }

    @NonNull
    @Override
    protected ReviewList read(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(ReviewListColumns.JSON));
        return getGson().fromJson(json, ReviewList.class);
    }

    @NonNull
    @Override
    protected ContentValues readRaw(Cursor cursor) {
        final String json = cursor.getString(cursor.getColumnIndex(ReviewListColumns.JSON));
        ContentValues contentValues = new ContentValues();
        contentValues.put(ReviewListColumns.JSON, json);
        return contentValues;
    }

    @NonNull
    @Override
    public Uri getUriForId(@NonNull Integer id) {
        Preconditions.checkNotNull(id, "Id cannot be null.");

        return RateBeerProvider.ReviewLists.withBeerId(id);
    }

    @Override
    protected boolean contentValuesEqual(ContentValues v1, ContentValues v2) {
        return v1.getAsString(ReviewListColumns.JSON).equals(v2.getAsString(ReviewListColumns.JSON));
    }
}
