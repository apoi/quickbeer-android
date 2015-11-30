package quickbeer.android.next.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reark.reark.utils.Log;
import quickbeer.android.next.data.DataLayer;
import rx.Observable;
import rx.Subscription;

/**
 * Created by antti on 16.11.2015.
 */
public class SearchAdapter extends BaseAdapter implements Filterable {
    private static final String TAG = SearchAdapter.class.getSimpleName();

    private List<String> adapterList = new ArrayList<>();
    private List<String> sourceList = new ArrayList<>();

    private Drawable suggestionIcon;
    private final LayoutInflater inflater;
    private final DataLayer.GetBeerSearchQueries beerSearchQueries;
    private final Observable<String> queryObservable;
    private Subscription oldSearchesSubscription;

    public SearchAdapter(Context context, DataLayer.GetBeerSearchQueries beerSearchQueries,
                         Observable<String> queryObservable) {
        this.inflater = LayoutInflater.from(context);
        this.beerSearchQueries = beerSearchQueries;
        this.queryObservable = queryObservable;

        refreshQueryList();
    }

    public void refreshQueryList() {
        if (oldSearchesSubscription != null) {
            oldSearchesSubscription.unsubscribe();
        }

        Observable<List<String>> oldSearches = beerSearchQueries.call();

        oldSearchesSubscription = Observable.combineLatest(oldSearches, queryObservable,
                (List<String> list, String query) -> {
                    list.add(query);
                    return list;
                })
                .startWith(oldSearches)
                .subscribe(
                        list -> {
                            Log.w(TAG, "got query list " + list.size());
                            sourceList = list;
                        },
                        throwable -> {
                            Log.e(TAG, "error", throwable);
                        });
    }

    @Override
    public int getCount() {
        return adapterList.size();
    }

    @Override
    public Object getItem(int position) {
        return adapterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence filter) {
                FilterResults results = new FilterResults();
                List<String> filtered = new ArrayList<>();

                if (!TextUtils.isEmpty(filter)) {
                    for (String value : sourceList) {
                        if (value.contains(filter)) {
                            filtered.add(value);
                        }
                    }
                }

                results.values = filtered;
                results.count = filtered.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence filter, FilterResults results) {
                if (results.count > 0) {
                    adapterList = (List<String>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(com.miguelcatalan.materialsearchview.R.layout.suggest_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        String currentListData = (String) getItem(position);
        mViewHolder.textView.setText(currentListData);

        return convertView;
    }

    private class MyViewHolder {
        TextView textView;
        ImageView imageView;

        public MyViewHolder(View convertView) {
            textView = (TextView) convertView.findViewById(com.miguelcatalan.materialsearchview.R.id.suggestion_text);
            if (suggestionIcon != null) {
                imageView = (ImageView) convertView.findViewById(com.miguelcatalan.materialsearchview.R.id.suggestion_icon);
                imageView.setImageDrawable(suggestionIcon);
            }
        }
    }
}
