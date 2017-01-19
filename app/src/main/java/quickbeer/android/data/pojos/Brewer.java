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

import static io.reark.reark.utils.Preconditions.get;

@SuppressWarnings("InnerClassReferencedViaSubclass")
@JsonAdapter(AutoValue_Brewer.GsonTypeAdapter.class)
@AutoValue
public abstract class Brewer {

    @NonNull
    @SerializedName("BrewerID")
    public abstract Integer id();

    @Nullable
    @SerializedName("BrewerName")
    public abstract String name();

    @Nullable
    @SerializedName("BrewerDescription")
    public abstract String description();

    @Nullable
    @SerializedName("BrewerAddress")
    public abstract String address();

    @Nullable
    @SerializedName("BrewerCity")
    public abstract String city();

    @Nullable
    @SerializedName("BrewerStateID")
    public abstract Integer stateId();

    @Nullable
    @SerializedName("BrewerCountryID")
    public abstract Integer countryId();

    @Nullable
    @SerializedName("BrewerZipCode")
    public abstract String zipCode();

    @Nullable
    @SerializedName("BrewerTypeID")
    public abstract Integer typeId();

    @Nullable
    @SerializedName("BrewerType")
    public abstract String type();

    @Nullable
    @SerializedName("BrewerWebSite")
    public abstract String website();

    @Nullable
    @SerializedName("Facebook")
    public abstract String facebook();

    @Nullable
    @SerializedName("Twitter")
    public abstract String twitter();

    @Nullable
    @SerializedName("BrewerEmail")
    public abstract String email();

    @Nullable
    @SerializedName("BrewerPhone")
    public abstract String phone();

    @Nullable
    @SerializedName("Barrels")
    public abstract Integer barrels();

    @Nullable
    @SerializedName("Opened")
    public abstract DateTime opened();

    @Nullable
    @SerializedName("EnteredOn")
    public abstract DateTime enteredOn();

    @Nullable
    @SerializedName("EnteredBy")
    public abstract Integer enteredBy();

    @Nullable
    @SerializedName("LogoImage")
    public abstract String logo();

    @Nullable
    @SerializedName("ViewCount")
    public abstract String viewCount();

    @Nullable
    @SerializedName("Score")
    public abstract Integer score();

    @Nullable
    @SerializedName("OOB")
    public abstract Boolean outOfBusiness();

    @Nullable
    @SerializedName("retired")
    public abstract Boolean retired();

    @Nullable
    @SerializedName("AreaCode")
    public abstract String areaCode();

    @Nullable
    @SerializedName("Hours")
    public abstract String hours();

    @Nullable
    @SerializedName("HeadBrewer")
    public abstract String headBrewer();

    @Nullable
    @SerializedName("MetroID")
    public abstract String metroId();

    @Nullable
    @SerializedName("MSA")
    public abstract String msa();

    @Nullable
    @SerializedName("RegionID")
    public abstract String regionId();

    // Accessors

    // Equality

    // Plumbing

    @NonNull
    public static TypeAdapter<Brewer> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_Brewer.GsonTypeAdapter(get(gson));
    }

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends OverwritableBuilder<AutoValue_Brewer.Builder> {

        public abstract Builder id(final Integer id);

        public abstract Builder name(@Nullable final String id);
        
        public abstract Builder description(@Nullable final String description);

        public abstract Builder address(@Nullable final String address);

        public abstract Builder city(@Nullable final String city);

        public abstract Builder stateId(@Nullable final Integer stateId);

        public abstract Builder countryId(@Nullable final Integer countryId);

        public abstract Builder zipCode(@Nullable final String zipCode);

        public abstract Builder typeId(@Nullable final Integer typeId);

        public abstract Builder type(@Nullable final String type);

        public abstract Builder website(@Nullable final String website);

        public abstract Builder facebook(@Nullable final String facebook);

        public abstract Builder twitter(@Nullable final String twitter);

        public abstract Builder email(@Nullable final String email);

        public abstract Builder phone(@Nullable final String phone);

        public abstract Builder barrels(@Nullable final Integer barrels);

        public abstract Builder opened(@Nullable final DateTime opened);

        public abstract Builder enteredOn(@Nullable final DateTime enteredOn);

        public abstract Builder enteredBy(@Nullable final Integer enteredBy);

        public abstract Builder logo(@Nullable final String logo);

        public abstract Builder viewCount(@Nullable final String viewCount);

        public abstract Builder score(@Nullable final Integer score);

        public abstract Builder outOfBusiness(@Nullable final Boolean outOfBusiness);

        public abstract Builder retired(@Nullable final Boolean retired);

        public abstract Builder areaCode(@Nullable final String areaCode);

        public abstract Builder hours(@Nullable final String hours);

        public abstract Builder headBrewer(@Nullable final String headBrewer);

        public abstract Builder metroId(@Nullable final String metroId);

        public abstract Builder msa(@Nullable final String msa);

        public abstract Builder regionId(@Nullable final String regionId);

        public abstract Brewer build();

        @NonNull
        @Override
        protected Class<AutoValue_Brewer.Builder> getTypeParameterClass() {
            return AutoValue_Brewer.Builder.class;
        }
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_Brewer.Builder();
    }

    @NonNull
    public static Builder builder(@NonNull final Brewer brewer) {
        return new AutoValue_Brewer.Builder(brewer);
    }

    @NonNull
    public static Brewer merge(@NonNull final Brewer v1, @NonNull final Brewer v2) {
        AutoValue_Brewer.Builder builder1 = new AutoValue_Brewer.Builder(get(v1));
        AutoValue_Brewer.Builder builder2 = new AutoValue_Brewer.Builder(get(v2));

        return builder1.overwrite(builder2).build();
    }
}
