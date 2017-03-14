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
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import quickbeer.android.data.pojos.base.OverwritableBuilder;
import quickbeer.android.utils.DateUtils;
import quickbeer.android.utils.StringUtils;

import static io.reark.reark.utils.Preconditions.get;

@SuppressWarnings("InnerClassReferencedViaSubclass")
@JsonAdapter(AutoValue_Review.GsonTypeAdapter.class)
@AutoValue
public abstract class Review {

    @NonNull
    @SerializedName("RatingID")
    public abstract Integer id();

    @Nullable
    @SerializedName("Appearance")
    public abstract Integer appearance();

    @Nullable
    @SerializedName("Aroma")
    public abstract Integer aroma();

    @Nullable
    @SerializedName("Flavor")
    public abstract Integer flavor();

    @Nullable
    @SerializedName("Mouthfeel")
    public abstract Integer mouthfeel();

    @Nullable
    @SerializedName("Overall")
    public abstract Integer overall();

    @Nullable
    @SerializedName("TotalScore")
    public abstract Float totalScore();

    @Nullable
    @SerializedName("Comments")
    public abstract String comments();

    @Nullable
    @SerializedName("TimeEntered")
    public abstract DateTime timeEntered();

    @Nullable
    @SerializedName("TimeUpdated")
    public abstract DateTime timeUpdated();

    @Nullable
    @SerializedName("UserID")
    public abstract Integer userID();

    @Nullable
    @SerializedName("UserName")
    public abstract String userName();

    @Nullable
    @SerializedName("City")
    public abstract String city();

    @Nullable
    @SerializedName("StateID")
    public abstract Integer stateID();

    @Nullable
    @SerializedName("State")
    public abstract String state();

    @Nullable
    @SerializedName("CountryID")
    public abstract Integer countryID();

    @Nullable
    @SerializedName("Country")
    public abstract String country();

    @Nullable
    @SerializedName("RateCount")
    public abstract Integer rateCount();

    // Accessors

    public String getDate() {
        return DateUtils.format(timeEntered());
    }

    public String getLocation() {
        String first = StringUtils.hasValue(city()) ? city() + ", " : "";
        String second = StringUtils.value(country(), "");

        return first + second;
    }

    // Plumbing

    @NonNull
    public static TypeAdapter<Review> typeAdapter(@NonNull Gson gson) {
        return new AutoValue_Review.GsonTypeAdapter(get(gson));
    }

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends OverwritableBuilder<AutoValue_Review.Builder> {

        public abstract Builder id(final Integer id);

        public abstract Builder appearance(@Nullable Integer appearance);

        public abstract Builder aroma(@Nullable Integer aroma);

        public abstract Builder flavor(@Nullable Integer flavor);

        public abstract Builder mouthfeel(@Nullable Integer mouthfeel);

        public abstract Builder overall(@Nullable Integer overall);

        public abstract Builder totalScore(@Nullable Float totalScore);

        public abstract Builder comments(@Nullable String comments);

        public abstract Builder timeEntered(@Nullable DateTime timeEntered);

        public abstract Builder timeUpdated(@Nullable DateTime timeUpdated);

        public abstract Builder userID(@Nullable Integer userID);

        public abstract Builder userName(@Nullable String userName);

        public abstract Builder city(@Nullable String city);

        public abstract Builder stateID(@Nullable Integer stateID);

        public abstract Builder state(@Nullable String state);

        public abstract Builder countryID(@Nullable Integer countryID);

        public abstract Builder country(@Nullable String country);

        public abstract Builder rateCount(@Nullable Integer rateCount);

        public abstract Review build();

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @NonNull
        @Override
        protected Class<AutoValue_Review.Builder> getTypeParameterClass() {
            return AutoValue_Review.Builder.class;
        }
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_Review.Builder();
    }

    @NonNull
    public static Builder builder(@NonNull Review beer) {
        return new AutoValue_Review.Builder(beer);
    }

    @NonNull
    public static Review merge(@NonNull Review v1, @NonNull Review v2) {
        AutoValue_Review.Builder builder1 = new AutoValue_Review.Builder(get(v1));
        AutoValue_Review.Builder builder2 = new AutoValue_Review.Builder(get(v2));

        return builder1.overwrite(builder2).build();
    }
}
