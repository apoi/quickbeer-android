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
package quickbeer.android.fragments.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import quickbeer.android.activities.base.BaseActivity;
import quickbeer.android.injections.FragmentComponent;
import quickbeer.android.injections.FragmentModule;

public abstract class BaseFragment extends Fragment {

    @Nullable
    private FragmentComponent component;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        inject();
    }

    @NonNull
    public FragmentComponent getComponent() {
        if (component == null) {
            component = ((BaseActivity) getActivity()).getComponent()
                    .plusFragment(new FragmentModule(this));
        }

        return component;
    }

    protected abstract void inject();

}
