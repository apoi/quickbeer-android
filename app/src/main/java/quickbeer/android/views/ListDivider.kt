/*
 * This file is part of QuickBeer.
 * Copyright (C) 2019 Antti Poikela <antti.poikela@iki.fi>
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

package quickbeer.android.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import quickbeer.android.R
import kotlin.math.roundToInt

class ListDivider(
    context: Context,
    @DrawableRes drawable: Int = R.drawable.divider,
    private val showLast: Boolean = true
) : DividerItemDecoration(context, VERTICAL) {

    private val bounds = Rect()
    private val divider = ContextCompat.getDrawable(context, drawable)!!

    init {
        setDrawable(divider)
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (showLast) {
            return super.onDraw(canvas, parent, state)
        }

        canvas.save()

        val left: Int
        val right: Int

        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(left, parent.paddingTop, right, parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }

        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i).also {
                parent.getDecoratedBoundsWithMargins(it, bounds)
            }
            val bottom = bounds.bottom + child.translationY.roundToInt()
            val top = bottom - divider.intrinsicHeight
            divider.setBounds(left, top, right, bottom)
            divider.draw(canvas)
        }

        canvas.restore()
    }
}
