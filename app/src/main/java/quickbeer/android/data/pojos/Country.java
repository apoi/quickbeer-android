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
package quickbeer.android.data.pojos;

import android.support.annotation.NonNull;

public class Country extends SimpleItem {
    private final int id;
    private final String name;
    private final String code;

    public Country(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    @Override
    public int getId() {
        return id;
    }

    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String getCode() {
        return code;
    }

    @NonNull
    public String getFlagResourceName() {
        switch (id) {
            case 239:
                return "flag_wales.png";
            case 240:
                return "flag_england.png";
            case 241:
                return "flag_scotland.png";
            default:
                return String.format("flag_%s.png", id);
        }
    }
}
