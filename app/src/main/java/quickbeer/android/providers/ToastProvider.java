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
package quickbeer.android.providers;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import static io.reark.reark.utils.Preconditions.get;

public class ToastProvider {

    @NonNull
    private final Context context;

    @Nullable
    private Toast currentToast;

    public ToastProvider(@NonNull Context context) {
        this.context = get(context);
    }

    public void showCancelableToast(@NonNull String text) {
        showCancelableToast(text, Toast.LENGTH_SHORT);
    }

    public void showCancelableToast(@NonNull String text, int length) {
        cancelIfCan();
        currentToast = Toast.makeText(context, text, length);
        currentToast.show();
    }

    public void showCancelableToast(@StringRes int stringId, int length) {
        cancelIfCan();
        currentToast = Toast.makeText(context, stringId, length);
        currentToast.show();
    }

    public void showToast(@NonNull String text) {
        showToast(text, Toast.LENGTH_SHORT);
    }

    public void showToast(@NonNull String text, int length) {
        cancelIfCan();
        Toast.makeText(context, text, length).show();
    }

    public void showToast(@StringRes int stringId) {
        showToast(stringId, Toast.LENGTH_SHORT);
    }

    public void showToast(@StringRes int stringId, int length) {
        cancelIfCan();
        Toast.makeText(context, stringId, length).show();
    }

    private void cancelIfCan() {
        if (currentToast != null) {
            currentToast.cancel();
            currentToast = null;
        }
    }

}
