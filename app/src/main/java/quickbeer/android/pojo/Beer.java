/**
 * This file is part of QuickBeer.
 * Copyright (C) 2016 Antti Poikela <antti.poikela@iki.fi>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General public abstract License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY(); without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General public abstract License for more details.
 *
 * You should have received a copy of the GNU General public abstract License
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

import java.io.IOException;
import java.util.Locale;

import io.reark.reark.utils.Log;
import quickbeer.android.pojo.base.MetadataAware;
import quickbeer.android.pojo.base.Overwriting;
import quickbeer.android.utils.Constants;

import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;

@SuppressWarnings("InnerClassReferencedViaSubclass")
@JsonAdapter(AutoValue_Beer.GsonTypeAdapter.class)
@AutoValue
public abstract class Beer implements MetadataAware<Beer> {

    private static final String TAG = Beer.class.getSimpleName();

    @NonNull
    @SerializedName("BeerID")
    public abstract Integer id();

    @Nullable
    @SerializedName("BeerName")
    public abstract String name();

    @Nullable
    @SerializedName("AverageRating")
    public abstract Float averageRating();

    @Nullable
    @SerializedName("OverallPctl")
    public abstract Float overallRating();

    @Nullable
    @SerializedName("StylePctl")
    public abstract Float styleRating();

    @Nullable
    @SerializedName("RateCount")
    public abstract Float rateCount();

    @Nullable
    @SerializedName("BeerStyleID")
    public abstract Integer styleId();

    @Nullable
    @SerializedName("BeerStyleName")
    public abstract String styleName();

    @Nullable
    @SerializedName("Alcohol")
    public abstract Float alcohol();

    @Nullable
    @SerializedName("IBU")
    public abstract Float ibu();

    @Nullable
    @SerializedName("Description")
    public abstract String description();

    @Nullable
    @SerializedName("IsAlias")
    public abstract Boolean isAlias();

    @Nullable
    @SerializedName("BrewerID")
    public abstract Integer brewerId();

    @Nullable
    @SerializedName("BrewerName")
    public abstract String brewerName();

    @Nullable
    @SerializedName("BrewerCountryId")
    public abstract Integer countryId();

    @Nullable
    @SerializedName("Liked")
    public abstract Integer tickValue();

    @Nullable
    @SerializedName("TimeEntered")
    public abstract DateTime tickDate();

    // Metadata

    @NonNull
    public abstract Metadata metadata();

    // Accessors

    public boolean hasDetails() {
        final String styleName = styleName();

        return brewerId() != null
                && styleName != null
                && !styleName.isEmpty();
    }

    public int rating() {
        return ofObj(overallRating())
                .map(Math::round)
                .orDefault(() -> -1);
    }

    public float getAbv() {
        return ofObj(alcohol()).orDefault(() -> -1.0f);
    }

    public String getImageUri() {
        return String.format(Locale.ROOT, Constants.BEER_IMAGE_PATH, id());
    }

    public boolean isTicked() {
        return getTickValue() > 0;
    }

    public int getTickValue() {
        return ofObj(tickValue()).orDefault(() -> -1);
    }

    // Equality

    @Override
    public boolean dataEquals(@NonNull final Beer other) {
        return false; // TODO how to do this nicely
    }

    @Override
    public boolean metadataEquals(@NonNull final Beer other) {
        return metadata().equals(other.metadata());
    }

    // Plumbing

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends Overwriting<AutoValue_Beer.Builder> {

        public abstract Builder id(final Integer id);

        public abstract Builder name(@Nullable final String name);

        public abstract Builder averageRating(@Nullable final Float averageRating);

        public abstract Builder overallRating(@Nullable final Float overallRating);

        public abstract Builder styleRating(@Nullable final Float styleRating);

        public abstract Builder rateCount(@Nullable final Float rateCount);

        public abstract Builder styleId(@Nullable final Integer styleId);

        public abstract Builder styleName(@Nullable final String styleName);

        public abstract Builder alcohol(@Nullable final Float alcohol);

        public abstract Builder ibu(@Nullable final Float ibu);

        public abstract Builder description(@Nullable final String description);

        public abstract Builder isAlias(@Nullable final Boolean isAlias);

        public abstract Builder brewerId(@Nullable final Integer brewerId);

        public abstract Builder brewerName(@Nullable final String brewerName);

        public abstract Builder countryId(@Nullable final Integer countryId);

        public abstract Builder tickValue(@Nullable final Integer tickValue);

        public abstract Builder tickDate(@Nullable final DateTime tickDate);

        public abstract Builder metadata(@Nullable final Metadata metadata);

        public abstract Beer build();

        @NonNull
        @Override
        protected Class<AutoValue_Beer.Builder> getTypeParameterClass() {
            return AutoValue_Beer.Builder.class;
        }
    }

    @NonNull
    public static TypeAdapter<Beer> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_Beer.GsonTypeAdapter(get(gson));
    }

    @NonNull
    public static Beer fromJson(@NonNull final String json, @NonNull final Gson gson) {
        try {
            return new AutoValue_Beer.GsonTypeAdapter(get(gson)).fromJson(get(json));
        } catch (IOException e) {
            Log.e(TAG, "Failed parsing json!", e);
            return builder().build();
        }
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_Beer.Builder();
    }

    @NonNull
    public static Builder builder(@NonNull final Beer beer) {
        return new AutoValue_Beer.Builder(beer);
    }

    @NonNull
    public static Beer merge(@NonNull final Beer v1, @NonNull final Beer v2) {
        AutoValue_Beer.Builder builder1 = new AutoValue_Beer.Builder(get(v1));
        AutoValue_Beer.Builder builder2 = new AutoValue_Beer.Builder(get(v2));

        return builder1.overwrite(builder2).build();
    }
}
