package quickbeer.android.next.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.List;

import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxViewBinder;
import quickbeer.android.next.R;
import quickbeer.android.next.adapters.BeerDetailsAdapter;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.viewmodels.BeerViewModel;
import quickbeer.android.next.viewmodels.ReviewListViewModel;
import quickbeer.android.next.viewmodels.ReviewViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by antti on 13.12.2015.
 */
public class BeerDetailsView extends FrameLayout {
    private RecyclerView beerDetailsListView;
    private BeerDetailsAdapter beerDetailsAdapter;

    public BeerDetailsView(Context context) {
        super(context);
    }

    public BeerDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void setBeer(@NonNull Beer beer) {
        Preconditions.checkNotNull(beer, "Beer cannot be null.");
        Preconditions.checkState(beerDetailsAdapter != null, "Beer details adapter cannot be null.");

        beerDetailsAdapter.setBeer(beer);
    }

    private void setReviews(@NonNull List<ReviewViewModel> reviews) {
        Preconditions.checkNotNull(reviews, "Reviews cannot be null.");
        Preconditions.checkState(beerDetailsAdapter != null, "Beer details adapter cannot be null.");

        beerDetailsAdapter.setReviews(reviews);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        beerDetailsAdapter = new BeerDetailsAdapter();
        beerDetailsListView = (RecyclerView) findViewById(R.id.beers_details_list_view);
        beerDetailsListView.setHasFixedSize(true);
        beerDetailsListView.setLayoutManager(new LinearLayoutManager(getContext()));
        beerDetailsListView.setAdapter(beerDetailsAdapter);
    }

    /**
     * View binder between BeerViewModel and the BeerDetailsView
     */
    public static class BeerViewBinder extends RxViewBinder {
        private BeerDetailsView view;
        private BeerViewModel viewModel;

        public BeerViewBinder(@NonNull BeerDetailsView view,
                              @NonNull BeerViewModel viewModel) {
            Preconditions.checkNotNull(view, "View cannot be null.");
            Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

            this.view = view;
            this.viewModel = viewModel;
        }

        @Override
        protected void bindInternal(@NonNull CompositeSubscription subscription) {
            subscription.add(viewModel.getBeer()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setBeer));
        }
    }

    /**
     * View binder between ReviewListViewModel and the BeerDetailsView
     */
    public static class ReviewListViewBinder extends RxViewBinder {
        private BeerDetailsView view;
        private ReviewListViewModel viewModel;

        public ReviewListViewBinder(@NonNull BeerDetailsView view,
                                    @NonNull ReviewListViewModel viewModel) {
            Preconditions.checkNotNull(view, "View cannot be null.");
            Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

            this.view = view;
            this.viewModel = viewModel;
        }

        @Override
        protected void bindInternal(@NonNull CompositeSubscription subscription) {
            subscription.add(viewModel.getReviews()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setReviews));
        }
    }
}
