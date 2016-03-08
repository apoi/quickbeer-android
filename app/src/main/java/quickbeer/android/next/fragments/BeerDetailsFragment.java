package quickbeer.android.next.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.R;
import quickbeer.android.next.activities.BeerDetailsActivity;
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.viewmodels.BeerViewModel;
import quickbeer.android.next.viewmodels.ReviewListViewModel;
import quickbeer.android.next.views.BeerDetailsView;

public class BeerDetailsFragment extends Fragment {
    private static final String TAG = BeerDetailsFragment.class.getSimpleName();

    private BeerViewModel beerViewModel;
    private BeerDetailsView.BeerViewBinder beerViewBinder;

    private ReviewListViewModel reviewListViewModel;
    private BeerDetailsView.ReviewListViewBinder reviewListViewBinder;

    @Inject
    DataLayer.GetBeer getBeer;

    @Inject
    DataLayer.GetReviews getReviews;

    @Inject
    DataLayer.GetReview getReview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QuickBeer.getInstance().getGraph().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.beer_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int beerId = ((BeerDetailsActivity) getActivity()).getBeerId();
        BeerDetailsView detailsView = (BeerDetailsView) view.findViewById(R.id.beer_details_view);

        beerViewModel = new BeerViewModel(beerId, getBeer);
        beerViewModel.subscribeToDataStore();
        beerViewBinder = new BeerDetailsView.BeerViewBinder(detailsView, beerViewModel);

        reviewListViewModel = new ReviewListViewModel(beerId, getReviews, getReview);
        reviewListViewModel.subscribeToDataStore();
        reviewListViewBinder = new BeerDetailsView.ReviewListViewBinder(detailsView, reviewListViewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        beerViewBinder.bind();
        reviewListViewBinder.bind();
    }

    @Override
    public void onPause() {
        super.onPause();
        beerViewBinder.unbind();
        reviewListViewBinder.unbind();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        beerViewModel.unsubscribeFromDataStore();
        reviewListViewModel.unsubscribeFromDataStore();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beerViewModel.dispose();
        beerViewModel = null;

        reviewListViewModel.dispose();
        reviewListViewModel = null;
    }
}
