package quickbeer.android.next.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxViewBinder;
import quickbeer.android.next.R;
import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.viewmodels.BeerViewModel;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by antti on 25.10.2015.
 */
public class BeerListAdapter extends RecyclerView.Adapter<BeerListAdapter.ViewHolder> {
    private static final String TAG = BeerListAdapter.class.getSimpleName();

    private final List<BeerViewModel> beers = new ArrayList<>();
    private View.OnClickListener onClickListener;

    public BeerListAdapter(List<BeerViewModel> beers) {
        this.beers.addAll(beers);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public BeerViewModel getItem(int position) {
        return beers.get(position);
    }

    @Override
    public BeerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_list_item, parent, false);
        return new ViewHolder(v, onClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(beers.get(position));
    }

    @Override
    public int getItemCount() {
        return beers.size();
    }

    public void set(List<BeerViewModel> beers) {
        this.beers.clear();
        this.beers.addAll(beers);

        notifyDataSetChanged();
    }

    /**
     * View holder for beer list items
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private ViewBinder viewBinder;

        public ViewHolder(View view, View.OnClickListener onClickListener) {
            super(view);

            this.nameTextView = (TextView) view.findViewById(R.id.beer_name);
            this.viewBinder = new ViewBinder(this);

            view.setOnClickListener(onClickListener);
        }

        public void bind(BeerViewModel viewModel) {
            clear();
            this.viewBinder.bind(viewModel);
        }

        public void setBeer(@NonNull Beer beer) {
            Preconditions.checkNotNull(beer, "Beer cannot be null.");

            nameTextView.setText(beer.getName());
        }

        public void clear() {
            nameTextView.setText("");
        }
    }

    /**
     * View binder between BeerViewModel and the view holder
     */
    private static class ViewBinder extends RxViewBinder {
        private ViewHolder viewHolder;
        private BeerViewModel viewModel;

        public ViewBinder(@NonNull ViewHolder viewHolder) {
            Preconditions.checkNotNull(viewHolder, "ViewHolder cannot be null.");

            this.viewHolder = viewHolder;
        }

        public void bind(@NonNull BeerViewModel viewModel) {
            Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

            this.viewModel = viewModel;
            this.viewModel.subscribeToDataStoreOnce();
            bind();
        }

        @Override
        protected void bindInternal(@NonNull CompositeSubscription subscription) {
            Preconditions.checkNotNull(viewModel, "ViewModel hasn't been set.");

            subscription.add(viewModel.getBeer()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(viewHolder::setBeer));
        }
    }
}
