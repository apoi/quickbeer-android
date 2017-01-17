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
package quickbeer.android.pojo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import quickbeer.android.pojo.base.Overwriting;
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
        return DateUtils.DATE_FORMAT.format(timeEntered());
    }

    public String getLocation() {
        String first = StringUtils.hasValue(city()) ? city() + ", " : "";
        String second = StringUtils.value(country(), "");

        return first + second;
    }

    // Plumbing

    @NonNull
    public static TypeAdapter<Review> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_Review.GsonTypeAdapter(get(gson));
    }

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends Overwriting<AutoValue_Review.Builder> {

        public abstract Builder id(final Integer id);

        public abstract Builder appearance(@Nullable final Integer appearance);

        public abstract Builder aroma(@Nullable final Integer aroma);

        public abstract Builder flavor(@Nullable final Integer flavor);

        public abstract Builder mouthfeel(@Nullable final Integer mouthfeel);

        public abstract Builder overall(@Nullable final Integer overall);

        public abstract Builder totalScore(@Nullable final Float totalScore);

        public abstract Builder comments(@Nullable final String comments);

        public abstract Builder timeEntered(@Nullable final DateTime timeEntered);

        public abstract Builder timeUpdated(@Nullable final DateTime timeUpdated);

        public abstract Builder userID(@Nullable final Integer userID);

        public abstract Builder userName(@Nullable final String userName);

        public abstract Builder city(@Nullable final String city);

        public abstract Builder stateID(@Nullable final Integer stateID);

        public abstract Builder state(@Nullable final String state);

        public abstract Builder countryID(@Nullable final Integer countryID);

        public abstract Builder country(@Nullable final String country);

        public abstract Builder rateCount(@Nullable final Integer rateCount);

        public abstract Review build();

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
    public static Builder builder(@NonNull final Review beer) {
        return new AutoValue_Review.Builder(beer);
    }

    @NonNull
    public static Review merge(@NonNull final Review v1, @NonNull final Review v2) {
        AutoValue_Review.Builder builder1 = new AutoValue_Review.Builder(get(v1));
        AutoValue_Review.Builder builder2 = new AutoValue_Review.Builder(get(v2));

        return builder1.overwrite(builder2).build();
    }
}
