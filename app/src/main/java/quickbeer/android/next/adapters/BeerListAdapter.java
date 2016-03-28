/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package quickbeer.android.next.adapters;

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
import quickbeer.android.next.utils.Score;
import quickbeer.android.next.viewmodels.BeerViewModel;
import quickbeer.android.next.views.listitems.ItemType;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class BeerListAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {
    private final List<BeerViewModel> beers = new ArrayList<>();
    private View.OnClickListener onClickListener;
    private int headerHeight = 0;

    public BeerListAdapter(List<BeerViewModel> beers) {
        this.beers.addAll(beers);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setHeaderHeight(int height) {
        this.headerHeight = height;
    }

    public BeerViewModel getItem(int position) {
        return beers.get(position - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ItemType.HEADER.ordinal()) {
            View v = new View(parent.getContext());
            v.setMinimumHeight(this.headerHeight);
            return new HeaderViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_list_item, parent, false);
            return new ViewHolder(v, onClickListener);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemType type = ItemType.values()[(getItemViewType(position))];

        if (type == ItemType.BEER) {
            ((ViewHolder) holder).bind(getItem(position));
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).unbind();
        }

        super.onViewRecycled(holder);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ItemType.HEADER.ordinal();
        } else {
            return ItemType.BEER.ordinal();
        }
    }

    @Override
    public int getItemCount() {
        return beers.size() + 1;
    }

    public void set(List<BeerViewModel> beers) {
        if (!beers.equals(this.beers)) {
            this.beers.clear();
            this.beers.addAll(beers);

            notifyDataSetChanged();
        }
    }

    /**
     * Simple view holder for the header
     */
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * View holder for beer list items
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private BeerViewModel viewModel;
        private ViewBinder viewBinder;
        private TextView ratingTextView;
        private TextView nameTextView;
        private TextView styleTextView;
        private TextView brewerTextView;

        public ViewHolder(View view, View.OnClickListener onClickListener) {
            super(view);

            this.ratingTextView = (TextView) view.findViewById(R.id.beer_stars);
            this.nameTextView = (TextView) view.findViewById(R.id.beer_name);
            this.styleTextView = (TextView) view.findViewById(R.id.beer_style);
            this.brewerTextView = (TextView) view.findViewById(R.id.brewer_name);

            view.setOnClickListener(onClickListener);
        }

        public void bind(BeerViewModel viewModel) {
            Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

            clear();

            this.viewModel = viewModel;
            this.viewModel.subscribeToDataStore();

            this.viewBinder = new ViewBinder(this, viewModel);
            this.viewBinder.bind();
        }

        public void unbind() {
            if (this.viewBinder != null) {
                this.viewBinder.unbind();
                this.viewBinder = null;
            }

            if (this.viewModel != null) {
                this.viewModel.unsubscribeFromDataStore();
                this.viewModel.dispose();
                this.viewModel = null;
            }
        }

        public void setBeer(@NonNull Beer beer) {
            Preconditions.checkNotNull(beer, "Beer cannot be null.");

            String rating = beer.getRating() >= 0
                    ? String.valueOf(beer.getRating())
                    : "?";

            ratingTextView.setBackgroundResource(Score.Stars.UNRATED.getResource());
            ratingTextView.setText(rating);
            nameTextView.setText(beer.getName());
            styleTextView.setText(beer.getStyleName());
            brewerTextView.setText(beer.getBrewerName());
        }

        public void clear() {
            ratingTextView.setBackground(null);
            ratingTextView.setText("");
            nameTextView.setText("");
            styleTextView.setText("");
            brewerTextView.setText("");
        }
    }

    /**
     * View binder between BeerViewModel and the view holder
     */
    private static class ViewBinder extends RxViewBinder {
        private ViewHolder viewHolder;
        private BeerViewModel viewModel;

        public ViewBinder(@NonNull ViewHolder viewHolder, @NonNull BeerViewModel viewModel) {
            Preconditions.checkNotNull(viewHolder, "ViewHolder cannot be null.");
            Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

            this.viewHolder = viewHolder;
            this.viewModel = viewModel;
        }

        @Override
        protected void bindInternal(@NonNull CompositeSubscription subscription) {
            subscription.add(viewModel.getBeer()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(viewHolder::setBeer));
        }
    }
}
