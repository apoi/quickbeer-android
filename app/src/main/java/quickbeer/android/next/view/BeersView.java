package quickbeer.android.next.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.tehmou.rxandroidarchitecture.utils.RxViewBinder;

import java.util.Collections;
import java.util.List;

import quickbeer.android.next.R;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.utils.Preconditions;
import quickbeer.android.next.viewmodels.BeersViewModel;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by antti on 25.10.2015.
 */
public class BeersView extends FrameLayout {

    private RecyclerView repositoriesListView;
    private BeerListAdapter beerListAdapter;

    public BeersView(Context context) {
        super(context, null);
    }

    public BeersView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        beerListAdapter = new BeerListAdapter(Collections.emptyList());

        repositoriesListView = (RecyclerView) findViewById(R.id.beers_list_view);
        repositoriesListView.setHasFixedSize(true);
        repositoriesListView.setLayoutManager(new LinearLayoutManager(getContext()));
        repositoriesListView.setAdapter(beerListAdapter);
    }

    private void setBeers(@NonNull List<Beer> beers) {
        Preconditions.checkNotNull(beers, "Beer list cannot be null.");
        Preconditions.checkState(beerListAdapter != null, "Beer list adapter cannot be null.");

        beerListAdapter.set(beers);
    }

    public static class ViewBinder extends RxViewBinder {
        private BeersView view;
        private BeersViewModel viewModel;

        public ViewBinder(@NonNull BeersView view,
                          @NonNull BeersViewModel viewModel) {
            Preconditions.checkNotNull(view, "View cannot be null.");
            Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

            this.view = view;
            this.viewModel = viewModel;
        }

        @Override
        protected void bindInternal(@NonNull CompositeSubscription subscription) {
            subscription.add(viewModel.getBeers()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::setBeers));

            subscription.add(Observable.create(
                    subscriber -> {
                        view.beerListAdapter.setOnClickListener(this::beerListAdapterOnClick);
                        subscriber.add(Subscriptions.create(() -> view.beerListAdapter.setOnClickListener(null)));
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }

        private void beerListAdapterOnClick(View clickedView) {
            final int itemPosition = view.repositoriesListView.getChildAdapterPosition(clickedView);
            Beer beer = view.beerListAdapter.getItem(itemPosition);
            viewModel.selectBeer(beer);
        }
    }
}
