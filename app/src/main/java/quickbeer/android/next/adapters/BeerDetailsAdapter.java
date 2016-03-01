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

import java.util.List;

import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxViewBinder;
import quickbeer.android.next.R;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.Review;
import quickbeer.android.next.utils.ContainerLabelExtractor;
import quickbeer.android.next.utils.StringUtils;
import quickbeer.android.next.viewmodels.ReviewViewModel;
import quickbeer.android.next.views.listitems.ItemType;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by antti on 13.12.2015.
 */
public class BeerDetailsAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

    private Beer beer;
    private List<ReviewViewModel> reviews;

    public BeerDetailsAdapter() {
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.BEER.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_details_view, parent, false);
            return new BeerDetailsViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_details_review, parent, false);
            return new ReviewViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemType type = ItemType.values()[(getItemViewType(position))];

        switch (type) {
            case BEER:
                ((BeerDetailsViewHolder) holder).setBeer(beer);
                break;
            case REVIEW:
                ((ReviewViewHolder) holder).bind(getItem(position));
                break;
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof ReviewViewHolder) {
            ((ReviewViewHolder) holder).unbind();
        }

        super.onViewRecycled(holder);
    }

    public ReviewViewModel getItem(int position) {
        return reviews.get(position - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ItemType.BEER.ordinal();
        } else {
            return ItemType.REVIEW.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        return (beer != null ? 1 : 0) +
                (reviews != null ? reviews.size() : 0);
    }

    public void setBeer(Beer beer) {
        if (!beer.equals(this.beer)) {
            this.beer = beer;

            notifyDataSetChanged();
        }
    }

    public void setReviews(List<ReviewViewModel> reviews) {
        if (!reviews.equals(this.reviews)) {
            this.reviews = reviews;

            notifyDataSetChanged();
        }
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
            descriptionTextView.setText(StringUtils.value(beer.getDescription(), "No description available."));

            imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    final int width = imageView.getMeasuredWidth();
                    final int height = imageView.getMeasuredHeight();

                    Picasso.with(imageView.getContext())
                            .load(beer.getImageUri())
                            .transform(new ContainerLabelExtractor(width, height))
                            .into(imageView);

                    return true;
                }
            });
        }
    }

    /**
     * View holder for reviews in list
     */
    protected static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private ReviewViewBinder reviewViewBinder;
        private TextView descriptionTextView;

        public ReviewViewHolder(View view) {
            super(view);

            this.reviewViewBinder = new ReviewViewBinder(this);
            this.descriptionTextView = (TextView) view.findViewById(R.id.review_description);
        }

        public void bind(ReviewViewModel viewModel) {
            clear();
            this.reviewViewBinder.bind(viewModel);
        }

        public void unbind() {
            this.reviewViewBinder.unbind();
        }


        public void setReview(@NonNull Review review) {
            Preconditions.checkNotNull(review, "Review cannot be null.");

            this.descriptionTextView.setText(review.getDescription());
        }

        public void clear() {
            this.descriptionTextView.setText("");
        }
    }

    /**
     * View binder between BeerViewModel and the view holder
     */
    private static class ReviewViewBinder extends RxViewBinder {
        private ReviewViewHolder viewHolder;
        private ReviewViewModel viewModel;

        public ReviewViewBinder(@NonNull ReviewViewHolder viewHolder) {
            Preconditions.checkNotNull(viewHolder, "ViewHolder cannot be null.");

            this.viewHolder = viewHolder;
        }

        public void bind(@NonNull ReviewViewModel viewModel) {
            Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

            this.viewModel = viewModel;
            this.viewModel.subscribeToDataStore();
            bind();
        }

        @Override
        protected void bindInternal(@NonNull CompositeSubscription subscription) {
            Preconditions.checkNotNull(viewModel, "ViewModel hasn't been setBeer.");

            subscription.add(viewModel.getReview()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(viewHolder::setReview));
        }
    }

}
