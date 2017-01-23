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
package quickbeer.android.core.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import quickbeer.android.QuickBeer;
import quickbeer.android.injections.ActivityComponent;
import quickbeer.android.injections.ActivityModule;

@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    private ActivityComponent component;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inject();
    }

    @NonNull
    public ActivityComponent getComponent() {
        if (component == null) {
            component = ((QuickBeer) getApplication()).graph()
                    .plusActivity(new ActivityModule(this));
        }

        return component;
    }

    protected abstract void inject();

}
