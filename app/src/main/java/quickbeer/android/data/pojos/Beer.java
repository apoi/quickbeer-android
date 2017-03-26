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
 * but WITHOUT ANY WARRANTY(); without even the implied warranty of
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

import org.threeten.bp.ZonedDateTime;

import java.io.IOException;
import java.util.Locale;

import quickbeer.android.Constants;
import quickbeer.android.data.pojos.base.OverwritableBuilder;
import timber.log.Timber;

import static io.reark.reark.utils.Preconditions.get;
import static polanski.option.Option.ofObj;
import static quickbeer.android.utils.StringUtils.hasValue;

@SuppressWarnings("InnerClassReferencedViaSubclass")
@JsonAdapter(AutoValue_Beer.GsonTypeAdapter.class)
@AutoValue
public abstract class Beer {

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
    public abstract ZonedDateTime tickDate();

    // Accessors

    public boolean hasDetails(boolean fullDetails) {
        //noinspection OverlyComplexBooleanExpression
        return brewerId() != null
                && hasValue(styleName())
                && (!fullDetails || description() != null);
    }

    public int rating() {
        return ofObj(overallRating())
                .filter(value -> value > 0.0)
                .map(Math::round)
                .orDefault(() -> -1);
    }

    public String getImageUri() {
        return String.format(Locale.ROOT, Constants.BEER_IMAGE_PATH, id());
    }

    public boolean isTicked() {
        return ofObj(tickValue()).orDefault(() -> -1) > 0;
    }

    // Plumbing

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends OverwritableBuilder<AutoValue_Beer.Builder> {

        public abstract Builder id(final Integer id);

        public abstract Builder name(@Nullable String name);

        public abstract Builder averageRating(@Nullable Float averageRating);

        public abstract Builder overallRating(@Nullable Float overallRating);

        public abstract Builder styleRating(@Nullable Float styleRating);

        public abstract Builder rateCount(@Nullable Float rateCount);

        public abstract Builder styleId(@Nullable Integer styleId);

        public abstract Builder styleName(@Nullable String styleName);

        public abstract Builder alcohol(@Nullable Float alcohol);

        public abstract Builder ibu(@Nullable Float ibu);

        public abstract Builder description(@Nullable String description);

        public abstract Builder isAlias(@Nullable Boolean isAlias);

        public abstract Builder brewerId(@Nullable Integer brewerId);

        public abstract Builder brewerName(@Nullable String brewerName);

        public abstract Builder countryId(@Nullable Integer countryId);

        public abstract Builder tickValue(@Nullable Integer tickValue);

        public abstract Builder tickDate(@Nullable ZonedDateTime tickDate);

        public abstract Beer build();

        @NonNull
        @Override
        protected Class<AutoValue_Beer.Builder> getTypeParameterClass() {
            return AutoValue_Beer.Builder.class;
        }
    }

    @NonNull
    public static TypeAdapter<Beer> typeAdapter(@NonNull Gson gson) {
        return new AutoValue_Beer.GsonTypeAdapter(get(gson));
    }

    @NonNull
    public static Beer fromJson(@NonNull String json, @NonNull Gson gson) {
        try {
            return new AutoValue_Beer.GsonTypeAdapter(get(gson)).fromJson(get(json));
        } catch (IOException e) {
            Timber.e(e, "Failed parsing json!");
            return builder().build();
        }
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_Beer.Builder();
    }

    @NonNull
    public static Builder builder(@NonNull Beer beer) {
        return new AutoValue_Beer.Builder(beer);
    }

    @NonNull
    public static Beer merge(@NonNull Beer v1, @NonNull Beer v2) {
        AutoValue_Beer.Builder builder1 = new AutoValue_Beer.Builder(get(v1));
        AutoValue_Beer.Builder builder2 = new AutoValue_Beer.Builder(get(v2));

        return builder1.overwrite(builder2).build();
    }
}
