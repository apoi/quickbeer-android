package quickbeer.android.next.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.reark.reark.utils.Preconditions;
import quickbeer.android.next.R;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.utils.BottleLabelExtractor;
import quickbeer.android.next.views.listitems.ItemType;

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
        if (beer != null) {
            return 1;
        } else {
            return 0;
        }
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
        private TextView abvTextView;
        private TextView brewerTextView;
        private TextView locationTextView;
        private TextView descriptionTextView;
        private ImageView imageView;

        public BeerDetailsViewHolder(View view) {
            super(view);

            this.ratingTextView = (TextView) view.findViewById(R.id.beer_stars);
            this.nameTextView = (TextView) view.findViewById(R.id.beer_name);
            this.styleTextView = (TextView) view.findViewById(R.id.beer_style);
            this.abvTextView = (TextView) view.findViewById(R.id.beer_abv);
            this.brewerTextView = (TextView) view.findViewById(R.id.brewer_name);
            this.locationTextView = (TextView) view.findViewById(R.id.brewer_location);
            this.descriptionTextView = (TextView) view.findViewById(R.id.beer_description);
            this.imageView = (ImageView) view.findViewById(R.id.beer_details_image);
        }

        public void setBeer(@NonNull Beer beer) {
            Preconditions.checkNotNull(beer, "Beer cannot be null.");

            ratingTextView.setText(String.valueOf(beer.getRating()));
            nameTextView.setText(beer.getName());
            styleTextView.setText(beer.getStyleName());
            abvTextView.setText(String.format("ABV: %.1f", beer.getAbv()));
            brewerTextView.setText(beer.getBrewerName());
            locationTextView.setText("TODO data from brewer");
            descriptionTextView.setText(beer.getDescription());

            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    final int width = imageView.getMeasuredWidth();
                    final int height = imageView.getMeasuredHeight();

                    Picasso.with(imageView.getContext())
                            .load(beer.getImageUri())
                            .transform(new BottleLabelExtractor(width, height))
                            .into(imageView);

                    return true;
                }
            });
        }
    }
}
