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

package quickbeer.android.listeners

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class RecyclerItemClickListener(
    context: Context,
    private val listener: (Int) -> Unit
) : RecyclerView.SimpleOnItemTouchListener() {

    private val tapDetector: GestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean = true
            override fun onDoubleTap(e: MotionEvent): Boolean = true
        })

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        view.findChildViewUnder(e.x, e.y)?.let {
            if (tapDetector.onTouchEvent(e)) {
                val adapterPosition = view.getChildAdapterPosition(it)
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.invoke(adapterPosition)
                }
            }
        }

        return false
    }
}
