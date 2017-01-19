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
package quickbeer.android.views.viewbinders;

import android.support.annotation.NonNull;

import io.reark.reark.utils.RxViewBinder;
import quickbeer.android.viewmodels.BeerViewModel;
import quickbeer.android.views.viewholders.BeerViewHolder;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static io.reark.reark.utils.Preconditions.get;

/**
 * View binder between BeerViewModel and the view holder
 */
public class BeerViewBinder extends RxViewBinder {
    private final BeerViewHolder viewHolder;
    private final BeerViewModel viewModel;

    public BeerViewBinder(@NonNull final BeerViewHolder viewHolder, @NonNull final BeerViewModel viewModel) {
        this.viewHolder = get(viewHolder);
        this.viewModel = get(viewModel);
    }

    @Override
    protected void bindInternal(@NonNull final CompositeSubscription subscription) {
        subscription.add(viewModel.getBeer()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(viewHolder::setBeer));
    }
}