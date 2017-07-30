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

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import quickbeer.android.data.pojos.base.OverwritableBuilder;

import static io.reark.reark.utils.Preconditions.get;

@SuppressWarnings("InnerClassReferencedViaSubclass")
@JsonAdapter(AutoValue_Country.GsonTypeAdapter.class)
@AutoValue
public abstract class Country {

    @SerializedName("id")
    public abstract int id();

    @NonNull
    @SerializedName("name")
    public abstract String name();

    @NonNull
    @SerializedName("official")
    public abstract String official();

    @NonNull
    @SerializedName("code")
    public abstract String code();

    @NonNull
    @SerializedName("refer")
    public abstract String refer();

    @NonNull
    @SerializedName("capital")
    public abstract String capital();

    @NonNull
    @SerializedName("region")
    public abstract String region();

    @NonNull
    @SerializedName("subregion")
    public abstract String subregion();

    @NonNull
    @SerializedName("wikipedia")
    public abstract String wikipedia();

    // Plumbing

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends OverwritableBuilder<AutoValue_Country.Builder> {

        public abstract Builder id(int value);

        public abstract Builder name(@NonNull String value);

        public abstract Builder official(@NonNull String value);

        public abstract Builder code(@NonNull String value);

        public abstract Builder refer(@NonNull String value);

        public abstract Builder capital(@NonNull String value);

        public abstract Builder region(@NonNull String value);

        public abstract Builder subregion(@NonNull String value);

        public abstract Builder wikipedia(@NonNull String value);

        public abstract Country build();

        @NonNull
        @Override
        protected Class<AutoValue_Country.Builder> getTypeParameterClass() {
            return AutoValue_Country.Builder.class;
        }
    }

    @NonNull
    public static TypeAdapter<Country> typeAdapter(@NonNull Gson gson) {
        return new AutoValue_Country.GsonTypeAdapter(get(gson));
    }

    public abstract Builder toBuilder();

    @NonNull
    public static Country.Builder builder() {
        return new AutoValue_Country.Builder();
    }

    @NonNull
    public static Country merge(@NonNull Country v1, @NonNull Country v2) {
        AutoValue_Country.Builder builder1 = (AutoValue_Country.Builder) get(v1).toBuilder();
        AutoValue_Country.Builder builder2 = (AutoValue_Country.Builder) get(v2).toBuilder();

        return builder1.overwrite(builder2).build();
    }

    public static class SimpleCountry extends SimpleItem {

        private final Country country;

        public SimpleCountry(@NonNull Country country) {
            this.country = get(country);
        }

        @Override
        public int getId() {
            return country.id();
        }

        @NonNull
        @Override
        public String getName() {
            return country.name();
        }

        @NonNull
        @Override
        public String getCode() {
            return country.code();
        }
    }
}
