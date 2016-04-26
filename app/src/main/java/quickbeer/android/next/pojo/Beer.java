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

import java.util.Date;

public class Beer extends BasePojo<Beer> {
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
    private Date updateDate;
    private Date accessDate;

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
        return overallRating > 0 ? Math.round(overallRating) : -1;
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
        return brewerId > 0
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

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date date) {
        updateDate = date;
    }

    public Date getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(Date date) {
        accessDate = date;
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
    public boolean dataEquals(Beer beer) {
        if (styleId != beer.styleId) return false;
        if (brewerId != beer.brewerId) return false;
        if (countryId != beer.countryId) return false;

        if (Float.compare(beer.averageRating, averageRating) != 0) return false;
        if (Float.compare(beer.overallRating, overallRating) != 0) return false;
        if (Float.compare(beer.styleRating, styleRating) != 0) return false;
        if (Float.compare(beer.rateCount, rateCount) != 0) return false;
        if (Float.compare(beer.alcohol, alcohol) != 0) return false;
        if (Float.compare(beer.ibu, ibu) != 0) return false;

        if (name != null ? !name.equals(beer.name) : beer.name != null) return false;
        if (styleName != null ? !styleName.equals(beer.styleName) : beer.styleName != null) return false;
        if (brewerName != null ? !brewerName.equals(beer.brewerName) : beer.brewerName != null) return false;
        if (description != null ? !description.equals(beer.description) : beer.description != null) return false;

        if (tick != beer.tick) return false;
        if (reviewId != beer.reviewId) return false;

        return isAlias == beer.isAlias;
    }

    // Metadata equality part.
    public boolean metadataEquals(Beer beer) {
        if (updateDate != null ? !updateDate.equals(beer.updateDate) : beer.updateDate != null) return false;
        if (accessDate != null ? !accessDate.equals(beer.accessDate) : beer.accessDate != null) return false;

        return isModified == beer.isModified;
    }

    @Override
    public int hashCode() {
        int result = id;

        result = 31 * result + styleId;
        result = 31 * result + brewerId;
        result = 31 * result + countryId;

        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (styleName != null ? styleName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (brewerName != null ? brewerName.hashCode() : 0);

        result = 31 * result + (averageRating != +0.0f ? Float.floatToIntBits(averageRating) : 0);
        result = 31 * result + (overallRating != +0.0f ? Float.floatToIntBits(overallRating) : 0);
        result = 31 * result + (styleRating != +0.0f ? Float.floatToIntBits(styleRating) : 0);
        result = 31 * result + (rateCount != +0.0f ? Float.floatToIntBits(rateCount) : 0);
        result = 31 * result + (alcohol != +0.0f ? Float.floatToIntBits(alcohol) : 0);
        result = 31 * result + (ibu != +0.0f ? Float.floatToIntBits(ibu) : 0);
        result = 31 * result + (isAlias ? 1 : 0);

        result = 31 * result + tick;
        result = 31 * result + reviewId;
        result = 31 * result + (isModified ? 1 : 0);
        result = 31 * result + (updateDate != null ? updateDate.hashCode() : 0);
        result = 31 * result + (accessDate != null ? accessDate.hashCode() : 0);

        return result;
    }
}
