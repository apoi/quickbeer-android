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
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class BottomMaskingLayout extends FrameLayout {
    private Path path = new Path();
    private int maskHeight = 0;

    public BottomMaskingLayout(Context context) {
        super(context);
    }

    public BottomMaskingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
    }

    public void setMaskingScrollView(RecyclerView view) {
        view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int scrollY = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollY += dy;

                setVisibleHeight(getHeight() - scrollY);
            }
        });
    }

    private void setVisibleHeight(int height) {
        height = Math.max(0, height);

        if (height != maskHeight) {
            maskHeight = height;
            path.reset();
            path.addRect(new RectF(0, 0, getWidth(), height), Path.Direction.CCW);

            invalidate();
        }
    }
}
