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
package quickbeer.android.next.views;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import quickbeer.android.next.R;
import quickbeer.android.next.utils.GraphicsUtils;

public class ProgressIndicatorBar extends FrameLayout {
    private View progressBar;

    public ProgressIndicatorBar(Context context) {
        super(context);
        initView();
    }

    public ProgressIndicatorBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        progressBar = new View(getContext());
        progressBar.setBackgroundColor(getResources().getColor(R.color.orange));
        progressBar.setMinimumWidth(GraphicsUtils.toPixels(30.0f, getResources()));

        addView(progressBar);
    }
}
