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

import quickbeer.android.next.pojo.base.Overwriting;
import quickbeer.android.next.utils.DateUtils;
import quickbeer.android.next.utils.StringUtils;

public class Review extends Overwriting<Review> {
    @SerializedName("RatingID")
    private int id;

    @SerializedName("Appearance")
    private Integer appearance;

    @SerializedName("Aroma")
    private Integer aroma;

    @SerializedName("Flavor")
    private Integer flavor;

    @SerializedName("Mouthfeel")
    private Integer mouthfeel;

    @SerializedName("Overall")
    private Integer overall;

    @SerializedName("TotalScore")
    private Float totalScore;

    @SerializedName("Comments")
    private String comments;

    @SerializedName("TimeEntered")
    private Date timeEntered;

    @SerializedName("TimeUpdated")
    private Date timeUpdated;

    @SerializedName("UserID")
    private Integer userID;

    @SerializedName("UserName")
    private String userName;

    @SerializedName("City")
    private String city;

    @SerializedName("StateID")
    private Integer stateID;

    @SerializedName("State")
    private String state;

    @SerializedName("CountryID")
    private Integer countryID;

    @SerializedName("Country")
    private String country;

    @SerializedName("RateCount")
    private Integer rateCount;

    private boolean isDraft;
    private boolean isModified;

    @NonNull
    @Override
    protected Class<Review> getTypeParameterClass() {
        return Review.class;
    }

    public int getId() {
        return id;
    }

    public float getRating() {
        return totalScore;
    }

    public String getDescription() {
        return comments;
    }

    public String getDate() {
        return DateUtils.DATE_FORMAT.format(timeEntered);
    }

    public String getReviewer() {
        return userName;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getLocation() {
        String first = StringUtils.hasValue(city) ? city + ", " : "";
        String second = StringUtils.value(country, "");

        return first + second;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setIsDraft(boolean draft) {
        isDraft = draft;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setIsModified(boolean modified) {
        isModified = modified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Review review = (Review) o;

        if (id != review.id) return false;
        if (isDraft != review.isDraft) return false;
        if (isModified != review.isModified) return false;

        if (appearance != null ? !appearance.equals(review.appearance) : review.appearance != null) return false;
        if (aroma != null ? !aroma.equals(review.aroma) : review.aroma != null) return false;
        if (flavor != null ? !flavor.equals(review.flavor) : review.flavor != null) return false;
        if (mouthfeel != null ? !mouthfeel.equals(review.mouthfeel) : review.mouthfeel != null) return false;
        if (overall != null ? !overall.equals(review.overall) : review.overall != null) return false;
        if (totalScore != null ? !totalScore.equals(review.totalScore) : review.totalScore != null) return false;
        if (comments != null ? !comments.equals(review.comments) : review.comments != null) return false;
        if (timeEntered != null ? !timeEntered.equals(review.timeEntered) : review.timeEntered != null) return false;
        if (timeUpdated != null ? !timeUpdated.equals(review.timeUpdated) : review.timeUpdated != null) return false;
        if (userID != null ? !userID.equals(review.userID) : review.userID != null) return false;
        if (userName != null ? !userName.equals(review.userName) : review.userName != null) return false;
        if (city != null ? !city.equals(review.city) : review.city != null) return false;
        if (stateID != null ? !stateID.equals(review.stateID) : review.stateID != null) return false;
        if (state != null ? !state.equals(review.state) : review.state != null) return false;
        if (countryID != null ? !countryID.equals(review.countryID) : review.countryID != null) return false;
        if (country != null ? !country.equals(review.country) : review.country != null) return false;

        return rateCount != null ? rateCount.equals(review.rateCount) : review.rateCount == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (appearance != null ? appearance.hashCode() : 0);
        result = 31 * result + (aroma != null ? aroma.hashCode() : 0);
        result = 31 * result + (flavor != null ? flavor.hashCode() : 0);
        result = 31 * result + (mouthfeel != null ? mouthfeel.hashCode() : 0);
        result = 31 * result + (overall != null ? overall.hashCode() : 0);
        result = 31 * result + (totalScore != null ? totalScore.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + (timeEntered != null ? timeEntered.hashCode() : 0);
        result = 31 * result + (timeUpdated != null ? timeUpdated.hashCode() : 0);
        result = 31 * result + (userID != null ? userID.hashCode() : 0);
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (stateID != null ? stateID.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (countryID != null ? countryID.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (rateCount != null ? rateCount.hashCode() : 0);
        result = 31 * result + (isDraft ? 1 : 0);
        result = 31 * result + (isModified ? 1 : 0);
        return result;
    }
}
