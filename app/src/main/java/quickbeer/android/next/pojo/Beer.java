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
package quickbeer.android.next.pojo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import quickbeer.android.next.pojo.base.MetadataAware;
import quickbeer.android.next.pojo.base.Overwriting;

import static io.reark.reark.utils.Preconditions.get;

@SuppressWarnings("InnerClassReferencedViaSubclass")
@AutoValue
public abstract class Beer implements MetadataAware<Beer> {

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
    public abstract Date tickDate();

    // Metadata

    @Nullable
    public abstract Integer reviewId();

    @Nullable
    public abstract Boolean isModified();

    @Nullable
    public abstract Date updateDate();

    @Nullable
    public abstract Date accessDate();

    // Accessors

    public boolean hasDetails() {
        final String styleName = styleName();

        return brewerId() != null
                && styleName != null
                && !styleName.isEmpty();
    }

    public int rating() {
        return overallRating() != null ? Math.round(overallRating()) : -1;
    }

    public float getAbv() {
        return alcohol() != null ? alcohol() : -1;
    }

    public String getImageUri() {
        return String.format("https://res.cloudinary.com/ratebeer/image/upload/w_250,c_limit/beer_%d.jpg", id());
    }

    public boolean isTicked() {
        return tickValue() > 0;
    }

    public int getTickValue() {
        return tickValue() != null ? tickValue() : -1;
    }

    // Equality

    @Override
    public boolean dataEquals(@NonNull final Beer other) {
        return false;
    }

    @Override
    public boolean metadataEquals(@NonNull final Beer other) {
        if (updateDate() != null ? !updateDate().equals(other.updateDate()) : other.updateDate()!= null) return false;
        if (accessDate() != null ? !accessDate().equals(other.accessDate()) : other.accessDate()!= null) return false;

        return isModified() == other.isModified();
    }

    // Plumbing

    @NonNull
    public static TypeAdapter<Beer> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_Beer.GsonTypeAdapter(get(gson));
    }

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

        public abstract Builder tickDate(@Nullable final Date tickDate);

        public abstract Builder reviewId(@Nullable final Integer reviewId);

        public abstract Builder isModified(@Nullable final Boolean isModified);

        public abstract Builder updateDate(@Nullable final Date updateDate);

        public abstract Builder accessDate(@Nullable final Date accessDate);

        public abstract Beer build();

        @NonNull
        @Override
        protected Class<AutoValue_Beer.Builder> getTypeParameterClass() {
            return AutoValue_Beer.Builder.class;
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
