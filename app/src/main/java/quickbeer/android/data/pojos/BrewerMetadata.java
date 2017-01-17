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
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.annotations.JsonAdapter;

import org.joda.time.DateTime;

import quickbeer.android.data.pojos.base.Overwriting;

import static io.reark.reark.utils.Preconditions.get;

@SuppressWarnings("InnerClassReferencedViaSubclass")
@AutoValue
public abstract class BrewerMetadata {

    private static final String TAG = BrewerMetadata.class.getSimpleName();

    @NonNull
    public abstract Integer brewerId();

    @NonNull
    public abstract DateTime updated();

    @NonNull
    public abstract DateTime accessed();

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends Overwriting<AutoValue_BrewerMetadata.Builder> {

        public abstract Builder brewerId(final Integer brewerId);

        public abstract Builder updated(@NonNull final DateTime updated);

        public abstract Builder accessed(@NonNull final DateTime accessed);

        public abstract BrewerMetadata build();

        @NonNull
        @Override
        protected Class<AutoValue_BrewerMetadata.Builder> getTypeParameterClass() {
            return AutoValue_BrewerMetadata.Builder.class;
        }
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_BrewerMetadata.Builder();
    }

    @NonNull
    public static Builder builder(@NonNull final BrewerMetadata metadata) {
        return new AutoValue_BrewerMetadata.Builder(metadata);
    }

    @NonNull
    public static BrewerMetadata merge(@NonNull final BrewerMetadata v1, @NonNull final BrewerMetadata v2) {
        AutoValue_BrewerMetadata.Builder builder1 = new AutoValue_BrewerMetadata.Builder(get(v1));
        AutoValue_BrewerMetadata.Builder builder2 = new AutoValue_BrewerMetadata.Builder(get(v2));

        return builder1.overwrite(builder2).build();
    }
}