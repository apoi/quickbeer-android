/*
 * Copyright 2016 Futurice GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package quickbeer.android.core.viewmodel.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.ViewModel;
import rx.subscriptions.CompositeSubscription;

import static io.reark.reark.utils.Preconditions.checkNotNull;
import static io.reark.reark.utils.Preconditions.get;

/**
 * Provides the base operations for a binding {@link android.support.v7.widget.RecyclerView.ViewHolder}
 *
 * Specific handling is required to support recycling.
 */
public abstract class BaseBindingViewHolder<T extends ViewModel>
        extends AbstractBindingViewHolder<T> {

    @Nullable
    private T viewModel;

    @NonNull
    private final CompositeSubscription subscription = new CompositeSubscription();

    protected BaseBindingViewHolder(@NonNull View view) {
        super(get(view));
    }

    @Override
    public final void bind(@NonNull T viewModel) {
        setAndBindDataModel(get(viewModel));
        bindViewToViewModel();
    }

    @Override
    public final void unbind() {
        unbindViewFromViewModel();
        unbindViewModelFromData();
    }

    @NonNull
    protected abstract DataBinder getViewDataBinder();

    @Nullable
    protected final T getViewModel() {
        return viewModel;
    }

    private void bindViewToViewModel() {
        getViewDataBinder().bind(subscription);
    }

    private void setAndBindDataModel(@NonNull T viewModel) {
        this.viewModel = viewModel;
        viewModel.bindToDataModel();
    }

    private void unbindViewFromViewModel() {
        // Don't dispose - we need to reuse it when recycling!
        subscription.clear();
        getViewDataBinder().unbind();
    }

    private void unbindViewModelFromData() {
        checkNotNull(viewModel, "View Model cannot be null when unbinding");
        viewModel.unbindDataModel();
        viewModel = null;
    }

}
