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
package quickbeer.android.core.activity

import android.os.Bundle
import androidx.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable

import quickbeer.android.core.viewmodel.BaseLifecycleViewDataBinder
import quickbeer.android.core.viewmodel.DataBinder
import quickbeer.android.core.viewmodel.ViewModel

/**
 * A base Activity which provides the binding mechanism hooks to a View Model.
 */
abstract class BindingDrawerActivity : InjectingDrawerActivity() {

    private val lifecycleBinder: BaseLifecycleViewDataBinder

    init {
        lifecycleBinder = object : BaseLifecycleViewDataBinder() {

            override fun bind(disposable: CompositeDisposable) {
                dataBinder().bind(disposable)
            }

            override fun unbind() {
                dataBinder().unbind()
            }

            override fun viewModel(): ViewModel {
                return this@BindingDrawerActivity.viewModel()
            }
        }
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleBinder.onCreate()
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        lifecycleBinder.onResume()
    }

    @CallSuper
    override fun onPause() {
        lifecycleBinder.onPause()
        super.onPause()
    }

    @CallSuper
    override fun onDestroy() {
        lifecycleBinder.onDestroyView()
        lifecycleBinder.onDestroy()
        super.onDestroy()
    }

    protected abstract fun viewModel(): ViewModel

    protected abstract fun dataBinder(): DataBinder
}
