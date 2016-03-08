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
package quickbeer.android.next.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

import com.squareup.picasso.Transformation;

import rx.functions.Func1;

public class ContainerLabelExtractor implements Transformation {

    private final int width;
    private final int height;

    public ContainerLabelExtractor(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        Bitmap result = getLabel(source, this.width, this.height);
        source.recycle();

        return result;
    }

    @Override
    public String key() {
        return String.format("label-%d-%d", this.width, this.height);
    }

    private static Bitmap getLabel(final Bitmap source, int width, int height) {
        Bitmap destination = Bitmap.createBitmap(width, height, source.getConfig());
        Rect destinationRect = new Rect(0, 0, destination.getWidth(), destination.getHeight());
        Canvas canvas = new Canvas(destination);

        final Rect sourceRect = cutToFit(getCentralRect(source), width, height);
        canvas.drawBitmap(source, sourceRect, destinationRect, null);

        return destination;
    }

    private static Rect cutToFit(final Rect source, int width, int height) {
        final float sourceRatio = (float) source.width() / source.height();
        final float targetRatio = (float) width / height;

        if (sourceRatio > targetRatio) {
            // Keep max height of source, cut sides to fit target size
            int diff = (int) (source.width() - (source.height() * targetRatio)) / 2;
            return new Rect(source.left + diff, source.top, source.right - diff, source.bottom);
        } else {
            // Keep max width of source, cut top and bottom to fit
            int diff = (int) (source.height() - (source.width() / targetRatio)) / 2;
            return new Rect(source.left, source.top + diff, source.right, source.bottom - diff);
        }
    }

    private static Rect getCentralRect(final Bitmap source) {
        // Reference point at 2/3 of the height where we expect the container to be thickest
        final int referenceY = (int) (source.getHeight() / 3.0f * 2.0f);
        final int minX = scanFromLeft(source, referenceY);
        final int maxX = scanFromRight(source, referenceY);

        // Maximum difference we allow from the reference point width scan is 7%
        // Success function: scan doesn't overshoot more than max difference.
        final int diff = (int) (source.getWidth() * 0.07f);
        Func1<Integer, Boolean> func = y -> scanFromLeft(source, y) < (minX + diff);

        // Do a binary search for the cut points above and below the reference point
        final int minY = binarySearch(referenceY, 0, func);
        final int maxY = binarySearch(referenceY, source.getHeight(), func);

        // Do a fixed cut from all sides to remove any blending with the background
        return new Rect(minX + diff, minY + diff, maxX - diff, maxY - diff);
    }

    private static int binarySearch(int from, int to, Func1<Integer, Boolean> func) {
        int middle = Math.max(to, from) - (Math.max(to, from) - Math.min(to, from)) / 2;

        if (Math.abs(from - to) == 1) {
            return from;
        } else if (func.call(middle)) {
            return binarySearch(middle, to, func);
        } else {
            return binarySearch(from, middle, func);
        }
    }

    private static int scanFromLeft(final Bitmap source, int y) {
        for (int x = 0; x < source.getWidth() / 2; x++) {
            if (!isWhitePixel(source, x, y)) {
                return x;
            }
        }

        return 0;
    }

    private static int scanFromRight(final Bitmap source, int y) {
        for (int x = source.getWidth() - 1; x > source.getWidth() / 2; x--) {
            if (!isWhitePixel(source, x, y)) {
                return x;
            }
        }

        return source.getWidth() - 1;
    }

    private static boolean isWhitePixel(final Bitmap source, int x, int y) {
        final int color = source.getPixel(x, y);
        final int r = Color.red(color);
        final int g = Color.green(color);
        final int b = Color.blue(color);

        return r > 190 && g > 190 && b > 190;
    }
}
