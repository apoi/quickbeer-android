/*
 * Copyright 2017 Futurice GmbH
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
package quickbeer.android.core.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import quickbeer.android.core.viewmodel.BaseLifecycleViewDataBinder;
import quickbeer.android.core.viewmodel.DataBinder;
import quickbeer.android.core.viewmodel.ViewModel;
import rx.subscriptions.CompositeSubscription;

/**
 * A base Fragment which provides the binding mechanism hooks to a View Model.
 */
public abstract class BindingBaseFragment extends BaseFragment {

    @NonNull
    private final BaseLifecycleViewDataBinder lifecycleBinder = new BaseLifecycleViewDataBinder() {

        @Override
        public void bind(@NonNull final CompositeSubscription subscription) {
            dataBinder().bind(subscription);
        }

        @Override
        public void unbind() {
            dataBinder().unbind();
        }

        @NonNull
        @Override
        public ViewModel viewModel() {
            return BindingBaseFragment.this.viewModel();
        }

    };

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lifecycleBinder.onCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
        lifecycleBinder.onResume();
    }

    @Override
    public void onPause() {
        lifecycleBinder.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        lifecycleBinder.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        lifecycleBinder.onDestroy();
        super.onDestroy();
    }

    @NonNull
    protected abstract ViewModel viewModel();

    @NonNull
    protected abstract DataBinder dataBinder();
}
