package quickbeer.android.next.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reark.reark.utils.Log;
import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxViewBinder;
import quickbeer.android.next.R;
import quickbeer.android.next.viewmodels.BeerListViewModel;
import quickbeer.android.next.viewmodels.BeerViewModel;
import quickbeer.android.next.views.listitems.MenuListItem;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;

/**
 * Created by antti on 25.10.2015.
 */
public class BeerListView extends FrameLayout {
    private static final String TAG = BeerListView.class.getSimpleName();

    private RecyclerView beersListView;
    private BeerListAdapter beerListAdapter;

    private List<MenuListItem> menuListItems;
    private RecyclerView menuListView;
    private MenuListAdapter menuListAdapter;

    public BeerListView(Context context) {
        super(context, null);
    }

    public BeerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        menuListItems = new ArrayList<>();
        menuListItems.add(new MenuListItem("search beers", R.drawable.score_unrated));
        menuListItems.add(new MenuListItem("best beers", R.drawable.score_unrated));
        menuListItems.add(new MenuListItem("brewers", R.drawable.score_unrated));
        menuListItems.add(new MenuListItem("scan barcode", R.drawable.score_unrated));

        menuListAdapter = new MenuListAdapter(menuListItems);
        menuListView = (RecyclerView) findViewById(R.id.menu_list_view);
        menuListView.setHasFixedSize(true);
        menuListView.setLayoutManager(new LinearLayoutManager(getContext()));
        menuListView.setAdapter(menuListAdapter);

        beerListAdapter = new BeerListAdapter(Collections.emptyList());
        beerListAdapter.setHeaderHeight(menuListView.getHeight());
        beersListView = (RecyclerView) findViewById(R.id.beers_list_view);
        beersListView.setHasFixedSize(true);
        beersListView.setLayoutManager(new LinearLayoutManager(getContext()));
        beersListView.setAdapter(beerListAdapter);

        // Redirect unhandled click events to menu
        beersListView.setOnTouchListener((v, event) -> {
            menuListView.dispatchTouchEvent(event);
            return false;
        });

        // No scrolling for the menu, override listener
        menuListView.setOnScrollListener(new RecyclerView.OnScrollListener() {});
    }

    private void setBeers(@NonNull List<BeerViewModel> beers) {
        Preconditions.checkNotNull(beers, "Beer list cannot be null.");
        Preconditions.checkState(beerListAdapter != null, "Beer list adapter cannot be null.");

        Log.v(TAG, "Settings " + beers.size() + " beers to adapter");
        beerListAdapter.set(beers);
    }

    /**
     * View binder between BeerListViewModel and the BeerListView
     */
    public static class ViewBinder extends RxViewBinder {
        private BeerListView view;
        private BeerListViewModel viewModel;

        public ViewBinder(@NonNull BeerListView view,
                          @NonNull BeerListViewModel viewModel) {
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
            final int itemPosition = view.beersListView.getChildAdapterPosition(clickedView);
            view.beerListAdapter
                    .getItem(itemPosition)
                    .getBeer()
                    .subscribe(beer -> {
                        viewModel.selectBeer(beer.getId());
                    });
        }
    }
}