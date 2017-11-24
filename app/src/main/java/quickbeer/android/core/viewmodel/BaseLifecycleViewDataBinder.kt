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
package quickbeer.android.core.viewmodel

import rx.subscriptions.CompositeSubscription

/**
 * Abstracts the view-to-viewmodel binding mechanism from the views associated lifecycle triggers.
 *
 * The View should implement a concrete instance to allow it bind to the time-variant (Observable)
 * values provided by its associated [ViewModel].
 */
abstract class BaseLifecycleViewDataBinder : LifecycleDataBinder {

    private val compositeSubscription = CompositeSubscription()

    /**
     * Inform the ViewModel that it needs to bind to the View's modelled data sources.
     */
    override fun onCreate() {}

    /**
     * Bind the View to the ViewModel's data sources.
     */
    override fun onResume() {
        bind(compositeSubscription)
        viewModel().bindToDataModel()
    }

    /**
     * Unbind the View from the ViewModel's data sources.
     */
    override fun onPause() {
        compositeSubscription.clear()
        unbind()
        viewModel().unbindDataModel()
    }

    /**
     * Inform the ViewModel that it needs to unbind from the View's modelled data sources.
     */
    override fun onDestroyView() {}

    /**
     * Permanently dispose of any resources held.
     *
     * The instance cannot be reused after this operation.
     */
    override fun onDestroy() {
        compositeSubscription.clear()
    }

    /**
     * The [ViewModel] to which this binder should bind/unbind.
     *
     * @return the [ViewModel] instance.
     */
    abstract fun viewModel(): ViewModel
}
