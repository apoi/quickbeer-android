/**
 * This file is part of QuickBeer.
 * Copyright (C) 2017 Antti Poikela <antti.poikela@iki.fi>
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
package quickbeer.android.utils;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import io.reark.reark.utils.Preconditions;
import polanski.option.Option;

import static io.reark.reark.utils.Preconditions.get;

public final class ViewUtils {

    public static Option<View> findView(@NonNull View view, @IdRes int id) {
        return Option.ofObj(get(view).findViewById(id));
    }

    public static Option<View> findView(@NonNull AppCompatDelegate delegate, @IdRes int id) {
        return Option.ofObj(get(delegate).findViewById(id));
    }

}
