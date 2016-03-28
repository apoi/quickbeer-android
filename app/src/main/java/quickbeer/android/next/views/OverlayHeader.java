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
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import quickbeer.android.next.R;

public class OverlayHeader extends FrameLayout {
    private View bottomFade;

    private int basicTranslation = 0;
    private int minimumTranslation;

    public OverlayHeader(Context context) {
        super(context);
        init();
    }

    public OverlayHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        minimumTranslation = (int) -getResources().getDimension(R.dimen.header_item_fade_height);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        bottomFade = findViewById(R.id.header_bottom_fade);
    }

    public void setMovingScrollView(RecyclerView view) {
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                addTranslation(-dy);
            }
        });
    }

    public void addTranslation(int translation) {
        basicTranslation += translation;

        int effectiveTranslation = Math.max(
                minimumTranslation,
                basicTranslation);

        setTranslationY(effectiveTranslation);

        if (effectiveTranslation == minimumTranslation) {
            bottomFade.setVisibility(VISIBLE);
        } else {
            bottomFade.setVisibility(INVISIBLE);
        }
    }
}
