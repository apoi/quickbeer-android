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
public abstract class BrewerMetadata {

    @NonNull
    public abstract Integer brewerId();

    @Nullable
    public abstract DateTime updated();

    @Nullable
    public abstract DateTime accessed();

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends OverwritableBuilder<AutoValue_BrewerMetadata.Builder> {

        public abstract Builder brewerId(final Integer brewerId);

        public abstract Builder updated(@Nullable DateTime updated);

        public abstract Builder accessed(@Nullable DateTime accessed);

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
    public static Builder builder(@NonNull BrewerMetadata metadata) {
        return new AutoValue_BrewerMetadata.Builder(metadata);
    }

    @NonNull
    public static BrewerMetadata newUpdate(int brewerId) {
        return builder()
                .brewerId(brewerId)
                .updated(DateTime.now())
                .build();
    }

    @NonNull
    public static BrewerMetadata newAccess(int brewerId) {
        return builder()
                .brewerId(brewerId)
                .accessed(DateTime.now())
                .build();
    }

    @NonNull
    public static BrewerMetadata merge(@NonNull BrewerMetadata v1, @NonNull BrewerMetadata v2) {
        AutoValue_BrewerMetadata.Builder builder1 = new AutoValue_BrewerMetadata.Builder(get(v1));
        AutoValue_BrewerMetadata.Builder builder2 = new AutoValue_BrewerMetadata.Builder(get(v2));

        return builder1.overwrite(builder2).build();
    }
}