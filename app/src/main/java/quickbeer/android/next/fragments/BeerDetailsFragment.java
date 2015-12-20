package quickbeer.android.next.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxViewBinder;
import quickbeer.android.next.QuickBeer;
import quickbeer.android.next.R;
import quickbeer.android.next.data.DataLayer;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.utils.Score;
import quickbeer.android.next.viewmodels.BeerViewModel;
import quickbeer.android.next.views.BeerDetailsView;
import quickbeer.android.next.views.BeerListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by antti on 9.12.2015.
 */
public class BeerDetailsFragment extends Fragment {
    private static final String TAG = BeerDetailsFragment.class.getSimpleName();

    private BeerViewModel viewModel;
    private BeerDetailsView.ViewBinder viewBinder;

    @Inject
    DataLayer.GetBeer getBeer;

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
        viewBinder = new BeerDetailsView.ViewBinder((BeerDetailsView) view.findViewById(R.id.beer_details_view), viewModel);
    }

    @Override
    public void onResume() {
        super.onResume();
        viewBinder.bind();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewBinder.unbind();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.unsubscribeFromDataStore();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewModel.dispose();
        viewModel = null;
    }
}
