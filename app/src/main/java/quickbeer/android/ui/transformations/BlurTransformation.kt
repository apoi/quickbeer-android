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

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import com.squareup.picasso.Transformation

class BlurTransformation(context: Context, private val radius: Int) : Transformation {

    private val rs: RenderScript = RenderScript.create(context)

    override fun transform(bitmap: Bitmap): Bitmap {
        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createTyped(rs, input.type)

        ScriptIntrinsicBlur.create(rs, Element.U8_4(rs)).apply {
            setInput(input)
            setRadius(radius.toFloat())
            forEach(output)
        }

        output.copyTo(bitmap)
        return bitmap
    }

    override fun key(): String {
        return "blur-$radius"
    }
}
