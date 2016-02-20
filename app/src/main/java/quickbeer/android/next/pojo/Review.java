package quickbeer.android.next.pojo;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reark.reark.pojo.OverwritablePojo;
import quickbeer.android.next.utils.StringUtils;

public class Review extends OverwritablePojo<Review> {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

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

    public float getRating() {
        return totalScore;
    }

    public String getDescription() {
        return comments;
    }

    public String getDate() {
        return DATE_FORMAT.format(timeEntered);
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
}
