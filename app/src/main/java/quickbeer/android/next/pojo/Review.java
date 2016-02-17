package quickbeer.android.next.pojo;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import io.reark.reark.pojo.OverwritablePojo;

public class Review extends OverwritablePojo<Review> {
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

    @NonNull
    @Override
    protected Class<Review> getTypeParameterClass() {
        return Review.class;
    }

    public int getId() {
        return id;
    }
}
