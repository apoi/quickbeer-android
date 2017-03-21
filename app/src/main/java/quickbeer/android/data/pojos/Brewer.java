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

import org.threeten.bp.ZonedDateTime;

import quickbeer.android.data.pojos.base.OverwritableBuilder;

import static io.reark.reark.utils.Preconditions.get;
import static quickbeer.android.utils.StringUtils.hasValue;

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
    public abstract ZonedDateTime founded();

    @Nullable
    @SerializedName("EnteredOn")
    public abstract ZonedDateTime enteredOn();

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

    public boolean hasDetails() {
        return hasValue(name());
    }

    // Equality

    // Plumbing

    @NonNull
    public static TypeAdapter<Brewer> typeAdapter(@NonNull Gson gson) {
        return new AutoValue_Brewer.GsonTypeAdapter(get(gson));
    }

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends OverwritableBuilder<AutoValue_Brewer.Builder> {

        public abstract Builder id(final Integer id);

        public abstract Builder name(@Nullable String id);
        
        public abstract Builder description(@Nullable String description);

        public abstract Builder address(@Nullable String address);

        public abstract Builder city(@Nullable String city);

        public abstract Builder stateId(@Nullable Integer stateId);

        public abstract Builder countryId(@Nullable Integer countryId);

        public abstract Builder zipCode(@Nullable String zipCode);

        public abstract Builder typeId(@Nullable Integer typeId);

        public abstract Builder type(@Nullable String type);

        public abstract Builder website(@Nullable String website);

        public abstract Builder facebook(@Nullable String facebook);

        public abstract Builder twitter(@Nullable String twitter);

        public abstract Builder email(@Nullable String email);

        public abstract Builder phone(@Nullable String phone);

        public abstract Builder barrels(@Nullable Integer barrels);

        public abstract Builder founded(@Nullable ZonedDateTime founded);

        public abstract Builder enteredOn(@Nullable ZonedDateTime enteredOn);

        public abstract Builder enteredBy(@Nullable Integer enteredBy);

        public abstract Builder logo(@Nullable String logo);

        public abstract Builder viewCount(@Nullable String viewCount);

        public abstract Builder score(@Nullable Integer score);

        public abstract Builder outOfBusiness(@Nullable Boolean outOfBusiness);

        public abstract Builder retired(@Nullable Boolean retired);

        public abstract Builder areaCode(@Nullable String areaCode);

        public abstract Builder hours(@Nullable String hours);

        public abstract Builder headBrewer(@Nullable String headBrewer);

        public abstract Builder metroId(@Nullable String metroId);

        public abstract Builder msa(@Nullable String msa);

        public abstract Builder regionId(@Nullable String regionId);

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
    public static Builder builder(@NonNull Brewer brewer) {
        return new AutoValue_Brewer.Builder(brewer);
    }

    @NonNull
    public static Brewer merge(@NonNull Brewer v1, @NonNull Brewer v2) {
        AutoValue_Brewer.Builder builder1 = new AutoValue_Brewer.Builder(get(v1));
        AutoValue_Brewer.Builder builder2 = new AutoValue_Brewer.Builder(get(v2));

        return builder1.overwrite(builder2).build();
    }
}
