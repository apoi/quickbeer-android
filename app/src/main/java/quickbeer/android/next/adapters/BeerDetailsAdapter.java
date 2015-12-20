package quickbeer.android.next.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxViewBinder;
import quickbeer.android.next.R;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.viewmodels.BaseViewModel;
import quickbeer.android.next.viewmodels.BeerViewModel;
import quickbeer.android.next.views.listitems.ItemType;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by antti on 13.12.2015.
 */
public class BeerDetailsAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

    private Beer beer;

    public BeerDetailsAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.BEER.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_details_view, parent, false);
            return new BeerDetailsViewHolder(v);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemType type = ItemType.values()[(getItemViewType(position))];

        switch (type) {
            case BEER:
            default:
                ((BeerDetailsViewHolder) holder).setBeer((Beer) getItem(position));
                break;
        }
    }

    public Object getItem(int position) {
        return beer;
    }

    @Override
    public int getItemViewType(int position) {
        return ItemType.BEER.ordinal();
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public void set(Beer beer) {
        this.beer = beer;

        notifyDataSetChanged();
    }

    /**
     * View holder for all the beer details
     */
    protected static class BeerDetailsViewHolder extends RecyclerView.ViewHolder {
        private TextView ratingTextView;
        private TextView nameTextView;
        private TextView styleTextView;
        private TextView brewerTextView;

        public BeerDetailsViewHolder(View view) {
            super(view);

            this.nameTextView = (TextView) view.findViewById(R.id.beer_name);
            this.brewerTextView = (TextView) view.findViewById(R.id.brewer_name);
            this.styleTextView = (TextView) view.findViewById(R.id.beer_style);
            this.ratingTextView = (TextView) view.findViewById(R.id.beer_stars);
        }

        public void setBeer(@NonNull Beer beer) {
            Preconditions.checkNotNull(beer, "Beer cannot be null.");

            nameTextView.setText(beer.getName());
            brewerTextView.setText(beer.getBrewerName());
            styleTextView.setText(beer.getStyleName());
            ratingTextView.setText(beer.getRating());
        }
    }
}
