package quickbeer.android.data.pojos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import quickbeer.android.data.pojos.base.OverwritableBuilder;

import static io.reark.reark.utils.Preconditions.get;

@SuppressWarnings("InnerClassReferencedViaSubclass")
@JsonAdapter(AutoValue_BeerStyle.GsonTypeAdapter.class)
@AutoValue
public abstract class BeerStyle {

    @NonNull
    @SerializedName("BeerStyleID")
    public abstract Integer id();

    @Nullable
    @SerializedName("BeerStyleName")
    public abstract String name();

    @Nullable
    @SerializedName("BeerStyleDescription")
    public abstract String description();

    @Nullable
    @SerializedName("BeerStyleParent")
    public abstract Integer parent();

    @Nullable
    @SerializedName("BeerStyleCategory")
    public abstract Integer category();

    // Plumbing

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends OverwritableBuilder<AutoValue_BeerStyle.Builder> {

        public abstract Builder id(final Integer id);

        public abstract Builder name(final String name);

        public abstract Builder description(final String description);

        public abstract Builder parent(final Integer parent);

        public abstract Builder category(final Integer category);

        public abstract BeerStyle build();

        @NonNull
        @Override
        protected Class<AutoValue_BeerStyle.Builder> getTypeParameterClass() {
            return AutoValue_BeerStyle.Builder.class;
        }
    }

    @NonNull
    public static TypeAdapter<BeerStyle> typeAdapter(@NonNull Gson gson) {
        return new AutoValue_BeerStyle.GsonTypeAdapter(get(gson));
    }

    @NonNull
    public static BeerStyle.Builder builder() {
        return new AutoValue_BeerStyle.Builder();
    }

    @NonNull
    public static BeerStyle.Builder builder(@NonNull BeerStyle style) {
        return new AutoValue_BeerStyle.Builder(style);
    }

    @NonNull
    public static BeerStyle merge(@NonNull BeerStyle v1, @NonNull BeerStyle v2) {
        AutoValue_BeerStyle.Builder builder1 = new AutoValue_BeerStyle.Builder(get(v1));
        AutoValue_BeerStyle.Builder builder2 = new AutoValue_BeerStyle.Builder(get(v2));

        return builder1.overwrite(builder2).build();
    }

}
