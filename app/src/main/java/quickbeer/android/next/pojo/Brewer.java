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

import quickbeer.android.next.utils.DateUtils;
import quickbeer.android.next.utils.StringUtils;

public class Brewer extends BasePojo<Brewer> {
    @SerializedName("BrewerID")
    private int id;

    @SerializedName("BrewerName")
    private String name;

    @SerializedName("BrewerDescription")
    private String description;

    @SerializedName("BrewerAddress")
    private String address;

    @SerializedName("BrewerCity")
    private String city;

    @SerializedName("BrewerStateID")
    private Integer stateId;

    @SerializedName("BrewerCountryID")
    private Integer countryId;

    @SerializedName("BrewerZipCode")
    private String zipCode;

    @SerializedName("BrewerTypeID")
    private Integer typeId;

    @SerializedName("BrewerType")
    private String type;

    @SerializedName("BrewerWebSite")
    private String website;

    @SerializedName("Facebook")
    private String facebook;

    @SerializedName("Twitter")
    private String twitter;

    @SerializedName("BrewerEmail")
    private String email;

    @SerializedName("BrewerPhone")
    private String phone;

    @SerializedName("Barrels")
    private Integer barrels;

    @SerializedName("Opened")
    private Date opened;

    @SerializedName("EnteredOn")
    private Date enteredOn;

    @SerializedName("EnteredBy")
    private Integer enteredBy;

    @SerializedName("LogoImage")
    private String logo;

    @SerializedName("ViewCount")
    private String viewCount;

    @SerializedName("Score")
    private Integer score;

    @SerializedName("OOB")
    private Boolean outOfBusiness;

    @SerializedName("retired")
    private Boolean retired;

    @SerializedName("AreaCode")
    private String areaCode;

    @SerializedName("Hours")
    private String hours;

    @SerializedName("HeadBrewer")
    private String headBrewer;

    @SerializedName("MetroID")
    private String metroId;

    @SerializedName("MSA")
    private String msa;

    @SerializedName("RegionID")
    private String regionId;

    private Date updateDate;
    private Date accessDate;

    @NonNull
    @Override
    protected Class<Brewer> getTypeParameterClass() {
        return Brewer.class;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return StringUtils.value(name);
    }

    @NonNull
    public String getDescription() {
        return StringUtils.value(description);
    }

    @NonNull
    public String getCity() {
        return StringUtils.value(city);
    }

    public int getCountryId() {
        return countryId != null ? countryId : -1;
    }

    @NonNull
    public String getType() {
        return StringUtils.value(type);
    }

    @NonNull
    public String getWebsite() {
        return StringUtils.value(website);
    }

    @NonNull
    public Date getOpened() {
        return DateUtils.value(opened);
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
        return "Brewer{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", countryId=" + countryId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Brewer brewer = (Brewer) o;

        if (id != brewer.id) return false;
        if (name != null ? !name.equals(brewer.name) : brewer.name != null) return false;
        if (description != null ? !description.equals(brewer.description) : brewer.description != null) return false;
        if (address != null ? !address.equals(brewer.address) : brewer.address != null) return false;
        if (city != null ? !city.equals(brewer.city) : brewer.city != null) return false;
        if (stateId != null ? !stateId.equals(brewer.stateId) : brewer.stateId != null) return false;
        if (countryId != null ? !countryId.equals(brewer.countryId) : brewer.countryId != null) return false;
        if (zipCode != null ? !zipCode.equals(brewer.zipCode) : brewer.zipCode != null) return false;
        if (typeId != null ? !typeId.equals(brewer.typeId) : brewer.typeId != null) return false;
        if (type != null ? !type.equals(brewer.type) : brewer.type != null) return false;
        if (website != null ? !website.equals(brewer.website) : brewer.website != null) return false;
        if (facebook != null ? !facebook.equals(brewer.facebook) : brewer.facebook != null) return false;
        if (twitter != null ? !twitter.equals(brewer.twitter) : brewer.twitter != null) return false;
        if (email != null ? !email.equals(brewer.email) : brewer.email != null) return false;
        if (phone != null ? !phone.equals(brewer.phone) : brewer.phone != null) return false;
        if (barrels != null ? !barrels.equals(brewer.barrels) : brewer.barrels != null) return false;
        if (opened != null ? !opened.equals(brewer.opened) : brewer.opened != null) return false;
        if (enteredOn != null ? !enteredOn.equals(brewer.enteredOn) : brewer.enteredOn != null) return false;
        if (enteredBy != null ? !enteredBy.equals(brewer.enteredBy) : brewer.enteredBy != null) return false;
        if (logo != null ? !logo.equals(brewer.logo) : brewer.logo != null) return false;
        if (viewCount != null ? !viewCount.equals(brewer.viewCount) : brewer.viewCount != null) return false;
        if (score != null ? !score.equals(brewer.score) : brewer.score != null) return false;
        if (outOfBusiness != null ? !outOfBusiness.equals(brewer.outOfBusiness) : brewer.outOfBusiness != null) return false;
        if (retired != null ? !retired.equals(brewer.retired) : brewer.retired != null) return false;
        if (areaCode != null ? !areaCode.equals(brewer.areaCode) : brewer.areaCode != null) return false;
        if (hours != null ? !hours.equals(brewer.hours) : brewer.hours != null) return false;
        if (headBrewer != null ? !headBrewer.equals(brewer.headBrewer) : brewer.headBrewer != null) return false;
        if (metroId != null ? !metroId.equals(brewer.metroId) : brewer.metroId != null) return false;
        if (msa != null ? !msa.equals(brewer.msa) : brewer.msa != null) return false;

        return regionId != null ? regionId.equals(brewer.regionId) : brewer.regionId == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (stateId != null ? stateId.hashCode() : 0);
        result = 31 * result + (countryId != null ? countryId.hashCode() : 0);
        result = 31 * result + (zipCode != null ? zipCode.hashCode() : 0);
        result = 31 * result + (typeId != null ? typeId.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (website != null ? website.hashCode() : 0);
        result = 31 * result + (facebook != null ? facebook.hashCode() : 0);
        result = 31 * result + (twitter != null ? twitter.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (barrels != null ? barrels.hashCode() : 0);
        result = 31 * result + (opened != null ? opened.hashCode() : 0);
        result = 31 * result + (enteredOn != null ? enteredOn.hashCode() : 0);
        result = 31 * result + (enteredBy != null ? enteredBy.hashCode() : 0);
        result = 31 * result + (logo != null ? logo.hashCode() : 0);
        result = 31 * result + (viewCount != null ? viewCount.hashCode() : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        result = 31 * result + (outOfBusiness != null ? outOfBusiness.hashCode() : 0);
        result = 31 * result + (retired != null ? retired.hashCode() : 0);
        result = 31 * result + (areaCode != null ? areaCode.hashCode() : 0);
        result = 31 * result + (hours != null ? hours.hashCode() : 0);
        result = 31 * result + (headBrewer != null ? headBrewer.hashCode() : 0);
        result = 31 * result + (metroId != null ? metroId.hashCode() : 0);
        result = 31 * result + (msa != null ? msa.hashCode() : 0);
        result = 31 * result + (regionId != null ? regionId.hashCode() : 0);
        return result;
    }
}
