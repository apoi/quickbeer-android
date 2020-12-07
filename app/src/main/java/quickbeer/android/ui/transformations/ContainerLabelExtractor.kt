/*
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
package quickbeer.android.ui.transformations

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import com.squareup.picasso.Transformation

class ContainerLabelExtractor(private val width: Int, private val height: Int) : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val result = getLabel(source, width, height)
        source.recycle()

        return result
    }

    override fun key(): String {
        return "label-%d-%d".format(width, height)
    }

    private fun getLabel(source: Bitmap, width: Int, height: Int): Bitmap {
        val destination = Bitmap.createBitmap(width, height, source.config)
        val destinationRect = Rect(0, 0, destination.width, destination.height)
        val canvas = Canvas(destination)

        val sourceRect = cutToFit(getCentralRect(source), width, height)
        canvas.drawBitmap(source, sourceRect, destinationRect, null)

        return destination
    }

    private fun cutToFit(source: Rect, width: Int, height: Int): Rect {
        val sourceRatio = source.width().toFloat() / source.height()
        val targetRatio = width.toFloat() / height

        if (sourceRatio > targetRatio) {
            // Keep max height of source, cut sides to fit target size
            val diff = (source.width() - source.height() * targetRatio).toInt() / 2
            return Rect(source.left + diff, source.top, source.right - diff, source.bottom)
        } else {
            // Keep max width of source, cut top and bottom to fit
            val diff = (source.height() - source.width() / targetRatio).toInt() / 2
            return Rect(source.left, source.top + diff, source.right, source.bottom - diff)
        }
    }

    private fun getCentralRect(source: Bitmap): Rect {
        // Reference point at 2/3 of the height where we expect the container to be thickest
        val referenceY = (source.height / 3.0f * 2.0f).toInt()
        val minX = scanFromLeft(source, referenceY)
        val maxX = scanFromRight(source, referenceY)

        // Maximum difference we allow from the reference point width scan is 7%
        // Success function: scan doesn't overshoot more than max difference.
        val diff = (source.width * 0.07f).toInt()
        val func: (y: Int) -> Boolean = { y -> scanFromLeft(source, y) < minX + diff }

        // Do a binary search for the cut points above and below the reference point
        val minY = binarySearch(referenceY, 0, func)
        val maxY = binarySearch(referenceY, source.height, func)

        // Do a fixed cut from all sides to remove any blending with the background
        return Rect(minX + diff, minY + diff, maxX - diff, maxY - diff)
    }

    private fun binarySearch(from: Int, to: Int, func: (Int) -> Boolean): Int {
        val middle = Math.max(to, from) - (Math.max(to, from) - Math.min(to, from)) / 2

        return if (Math.abs(from - to) == 1) {
            from
        } else if (func(middle)) {
            binarySearch(middle, to, func)
        } else {
            binarySearch(from, middle, func)
        }
    }

    private fun scanFromLeft(source: Bitmap, y: Int): Int {
        for (x in 0 until source.width / 2) {
            if (!isWhitePixel(source, x, y)) {
                return x
            }
        }

        return 0
    }

    private fun scanFromRight(source: Bitmap, y: Int): Int {
        for (x in source.width - 1 downTo source.width / 2 + 1) {
            if (!isWhitePixel(source, x, y)) {
                return x
            }
        }

        return source.width - 1
    }

    private fun isWhitePixel(source: Bitmap, x: Int, y: Int): Boolean {
        val color = source.getPixel(x, y)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        return r > 190 && g > 190 && b > 190
    }
}
