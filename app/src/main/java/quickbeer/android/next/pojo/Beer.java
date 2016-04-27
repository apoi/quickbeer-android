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
package quickbeer.android.next.pojo;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import quickbeer.android.next.pojo.base.AccessTrackingItem;

public class Beer extends AccessTrackingItem<Beer> {
    @SerializedName("BeerID")
    private int id;

    @SerializedName("BeerName")
    private String name;

    @SerializedName("AverageRating")
    private Float averageRating;

    @SerializedName("OverallPctl")
    private Float overallRating;

    @SerializedName("StylePctl")
    private Float styleRating;

    @SerializedName("RateCount")
    private Float rateCount;

    @SerializedName("BeerStyleID")
    private Integer styleId;

    @SerializedName("BeerStyleName")
    private String styleName;

    @SerializedName("Alcohol")
    private Float alcohol;

    @SerializedName("IBU")
    private Float ibu;

    @SerializedName("Description")
    private String description;

    @SerializedName("IsAlias")
    private Boolean isAlias;

    @SerializedName("BrewerID")
    private Integer brewerId;

    @SerializedName("BrewerName")
    private String brewerName;

    @SerializedName("BrewerCountryId")
    private Integer countryId;

    private int tick;
    private int reviewId;
    private boolean isModified;

    @NonNull
    @Override
    protected Class<Beer> getTypeParameterClass() {
        return Beer.class;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getStyleName() {
        return styleName;
    }

    @NonNull
    public String getBrewerName() {
        return brewerName;
    }

    public int getRating() {
        return overallRating != null ? Math.round(overallRating) : -1;
    }

    public float getAbv() {
        return alcohol;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUri() {
        return String.format("https://res.cloudinary.com/ratebeer/image/upload/w_250,c_limit/beer_%d.jpg", getId());
    }

    public boolean hasDetails() {
        return brewerId != null
                && styleName != null
                && !styleName.isEmpty();
    }

    public boolean isTicked() {
        return tick > 0;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int value) {
        tick = value;
    }

    public boolean isReviewed() {
        return reviewId > 0;
    }

    public int getReviewId() {
        return reviewId;
    }

    public void setReviewId(int value) {
        reviewId = value;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setIsModified(boolean value) {
        isModified = value;
    }

    @Override
    public String toString() {
        return "Beer{" + "id=" + id
                + ", name='" + name + '\''
                + ", style='" + styleName + '\''
                + ", brewer='" + brewerName + '\''
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Beer beer = (Beer) o;
        if (id != beer.id) return false;

        return metadataEquals(beer) && dataEquals(beer);
    }

    // Function for checking the data equality. Useful when we want to check only the data
    // parts, leaving out e.g. the beer access date. Note that we need to check every field:
    // this is an object comparison, not beer identity comparison.
    @Override
    public boolean dataEquals(Beer beer) {
        if (name != null ? !name.equals(beer.name) : beer.name != null) return false;
        if (styleName != null ? !styleName.equals(beer.styleName) : beer.styleName != null) return false;
        if (description != null ? !description.equals(beer.description) : beer.description != null) return false;

        if (styleId != null ? !styleId.equals(beer.styleId) : beer.styleId != null) return false;
        if (brewerId != null ? !brewerId.equals(beer.brewerId) : beer.brewerId != null) return false;
        if (countryId != null ? !countryId.equals(beer.countryId) : beer.countryId != null) return false;

        if (ibu != null ? !ibu.equals(beer.ibu) : beer.ibu != null) return false;
        if (alcohol != null ? !alcohol.equals(beer.alcohol) : beer.alcohol != null) return false;
        if (brewerName != null ? !brewerName.equals(beer.brewerName) : beer.brewerName != null) return false;

        if (averageRating != null ? !averageRating.equals(beer.averageRating) : beer.averageRating != null) return false;
        if (overallRating != null ? !overallRating.equals(beer.overallRating) : beer.overallRating != null) return false;
        if (styleRating != null ? !styleRating.equals(beer.styleRating) : beer.styleRating != null) return false;
        if (rateCount != null ? !rateCount.equals(beer.rateCount) : beer.rateCount != null) return false;

        if (tick != beer.tick) return false;
        if (reviewId != beer.reviewId) return false;

        return isAlias != null ? isAlias.equals(beer.isAlias) : beer.isAlias == null;
    }

    @Override
    public boolean metadataEquals(Beer beer) {
        return super.metadataEquals(beer)
                && isModified == beer.isModified;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (averageRating != null ? averageRating.hashCode() : 0);
        result = 31 * result + (overallRating != null ? overallRating.hashCode() : 0);
        result = 31 * result + (styleRating != null ? styleRating.hashCode() : 0);
        result = 31 * result + (rateCount != null ? rateCount.hashCode() : 0);
        result = 31 * result + (styleId != null ? styleId.hashCode() : 0);
        result = 31 * result + (styleName != null ? styleName.hashCode() : 0);
        result = 31 * result + (alcohol != null ? alcohol.hashCode() : 0);
        result = 31 * result + (ibu != null ? ibu.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (isAlias != null ? isAlias.hashCode() : 0);
        result = 31 * result + (brewerId != null ? brewerId.hashCode() : 0);
        result = 31 * result + (brewerName != null ? brewerName.hashCode() : 0);
        result = 31 * result + (countryId != null ? countryId.hashCode() : 0);
        result = 31 * result + tick;
        result = 31 * result + reviewId;
        result = 31 * result + (isModified ? 1 : 0);
        return result;
    }
}
