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
package quickbeer.android.next.views.viewbinders;

import android.support.annotation.NonNull;

import io.reark.reark.utils.Preconditions;
import io.reark.reark.utils.RxViewBinder;
import quickbeer.android.next.viewmodels.BeerViewModel;
import quickbeer.android.next.views.viewholders.BeerViewHolder;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * View binder between BeerViewModel and the view holder
 */
public class BeerViewBinder extends RxViewBinder {
    private final BeerViewHolder viewHolder;
    private final BeerViewModel viewModel;

    public BeerViewBinder(@NonNull final BeerViewHolder viewHolder, @NonNull final BeerViewModel viewModel) {
        Preconditions.checkNotNull(viewHolder, "ViewHolder cannot be null.");
        Preconditions.checkNotNull(viewModel, "ViewModel cannot be null.");

        this.viewHolder = viewHolder;
        this.viewModel = viewModel;
    }

    @Override
    protected void bindInternal(@NonNull final CompositeSubscription subscription) {
        subscription.add(viewModel.getBeer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(viewHolder::setBeer));
    }
}