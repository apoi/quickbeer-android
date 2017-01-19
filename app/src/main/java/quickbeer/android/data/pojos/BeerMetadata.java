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

import org.joda.time.DateTime;

import quickbeer.android.data.pojos.base.OverwritableBuilder;

import static io.reark.reark.utils.Preconditions.get;

@SuppressWarnings("InnerClassReferencedViaSubclass")
@AutoValue
public abstract class BeerMetadata {

    private static final String TAG = BeerMetadata.class.getSimpleName();

    @NonNull
    public abstract Integer beerId();

    @Nullable
    public abstract DateTime updated();

    @Nullable
    public abstract DateTime accessed();

    @Nullable
    public abstract Integer reviewId();

    @Nullable
    public abstract Boolean isModified();

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends OverwritableBuilder<AutoValue_BeerMetadata.Builder> {

        public abstract Builder beerId(final Integer beerId);

        public abstract Builder updated(@Nullable final DateTime updated);

        public abstract Builder accessed(@Nullable final DateTime accessed);

        public abstract Builder reviewId(@Nullable final Integer reviewId);

        public abstract Builder isModified(@Nullable final Boolean isModified);

        public abstract BeerMetadata build();

        @NonNull
        @Override
        protected Class<AutoValue_BeerMetadata.Builder> getTypeParameterClass() {
            return AutoValue_BeerMetadata.Builder.class;
        }
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_BeerMetadata.Builder();
    }

    @NonNull
    public static Builder builder(@NonNull final BeerMetadata metadata) {
        return new AutoValue_BeerMetadata.Builder(metadata);
    }

    @NonNull
    public static BeerMetadata newUpdate(@NonNull final Beer beer) {
        return builder()
                .beerId(beer.id())
                .updated(DateTime.now())
                .build();
    }

    @NonNull
    public static BeerMetadata newAccess(@NonNull final Beer beer) {
        return builder()
                .beerId(beer.id())
                .accessed(DateTime.now())
                .build();
    }

    @NonNull
    public static BeerMetadata merge(@NonNull final BeerMetadata v1, @NonNull final BeerMetadata v2) {
        AutoValue_BeerMetadata.Builder builder1 = new AutoValue_BeerMetadata.Builder(get(v1));
        AutoValue_BeerMetadata.Builder builder2 = new AutoValue_BeerMetadata.Builder(get(v2));

        return builder1.overwrite(builder2).build();
    }
}