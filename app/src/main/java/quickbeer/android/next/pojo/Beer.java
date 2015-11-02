package quickbeer.android.next.pojo;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by antti on 17.10.2015.
 */
public class Beer {
    @SerializedName("BeerID")
    private int id;

    @SerializedName("BeerName")
    private String name;

    @SerializedName("AverageRating")
    private float averageRating;

    @SerializedName("OverallPctl")
    private float overallRating;

    @SerializedName("StylePctl")
    private float styleRating;

    @SerializedName("RateCount")
    private float rateCount;

    @SerializedName("BeerStyleID")
    private int styleId;

    @SerializedName("BeerStyleName")
    private String styleName;

    @SerializedName("Alcohol")
    private float alcohol;

    @SerializedName("IBU")
    private float ibu;

    @SerializedName("Description")
    private String description;

    @SerializedName("IsAlias")
    private boolean isAlias;

    @SerializedName("BrewerID")
    private int brewerId;

    @SerializedName("BrewerName")
    private String brewerName;

    @SerializedName("BrewerCountryId")
    private int countryId;

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

    @NonNull
    public int getRating() {
        return Math.round(overallRating);
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
        } else if (!(o instanceof Beer)) {
            return false;
        }

        Beer that = (Beer) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }
}

