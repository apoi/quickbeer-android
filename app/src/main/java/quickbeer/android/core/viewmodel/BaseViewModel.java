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
package quickbeer.android.core.viewmodel;

import android.support.annotation.NonNull;

import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public abstract class BaseViewModel implements ViewModel {

    @NonNull
    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private boolean isBound;

    @Override
    public final void bindToDataModel() {
        if (!isBound) {
            bind(compositeSubscription);
            isBound = true;
        }
    }

    @Override
    public final void unbindDataModel() {
        if (isBound) {
            compositeSubscription.clear();
            unbind();
            isBound = false;
        }
    }

    protected abstract void bind(@NonNull CompositeSubscription subscription);

    protected abstract void unbind();

    @Override
    public final void dispose() {
        if (isBound) {
            Timber.e("Disposing before unbind!");
            unbindDataModel();
        }
    }
}
