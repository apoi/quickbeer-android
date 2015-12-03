package quickbeer.android.next.pojo;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import io.reark.reark.utils.Log;

/**
 * Created by antti on 17.10.2015.
 */
public class Beer {
    private static final String TAG = Beer.class.getSimpleName();

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
        return overallRating > 0 ? Math.round(overallRating) : -1;
    }

    public Beer overwrite(Beer other) {
        if (id != other.id) {
            return other;
        } else if (equals(other)) {
            return this;
        }

        for (Field field : Beer.class.getDeclaredFields()) {
            final int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)) {
                continue;
            }

            try {
                if (!isEmpty(field, other)) {
                    field.set(this, field.get(other));
                }
            } catch (IllegalAccessException e) {
                Log.e(TAG, "Failed set at " + field.getName(), e);
            }
        }

        return this;
    }

    private static boolean isEmpty(Field field, Beer beer) {
        try {
            Object value = field.get(beer);
            if (value == null) {
                return true;
            } if (value instanceof String) {
                return isEmpty((String) value);
            } else if (value instanceof Integer) {
                return isEmpty((Integer) value);
            } else if (value instanceof Float) {
                return isEmpty((Float) value);
            } else if (value instanceof Boolean) {
                return false;
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "Failed get at " + field.getName(), e);
        }

        Log.w(TAG, "Unknown field type: " + field.getName());
        return true;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    private static boolean isEmpty(float value) {
        return value <= 0.0f;
    }

    private static boolean isEmpty(int value) {
        return value <= 0;
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

        return (isAlias != beer.isAlias);
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
        return result;
    }
}

