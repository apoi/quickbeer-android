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
package quickbeer.android.data.pojos

import com.google.gson.annotations.SerializedName
import quickbeer.android.data.pojos.base.Overwritable

data class User(@SerializedName("id") val id: Int,
                @SerializedName("username") val username: String,
                @SerializedName("password") val password: String)
    : Overwritable<User>() {

    override fun getTypeParameterClass(): Class<User> {
        return User::class.java
    }

    companion object {
        fun merge(v1: User, v2: User): User {
            val user = v1.copy()
            user.overwrite(v2)
            return user
        }
    }
}
