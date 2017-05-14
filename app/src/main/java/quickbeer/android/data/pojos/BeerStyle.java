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

    @SerializedName("BeerStyleID")
    public abstract int id();

    @NonNull
    @SerializedName("BeerStyleName")
    public abstract String name();

    @NonNull
    @SerializedName("BeerStyleDescription")
    public abstract String description();

    @SerializedName("BeerStyleParent")
    public abstract int parent();

    @SerializedName("BeerStyleCategory")
    public abstract int category();

    // Plumbing

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends OverwritableBuilder<AutoValue_BeerStyle.Builder> {

        public abstract Builder id(int id);

        public abstract Builder name(@NonNull String name);

        public abstract Builder description(@NonNull String description);

        public abstract Builder parent(int parent);

        public abstract Builder category(int category);

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

    public static class SimpleStyle extends SimpleItem {

        private final BeerStyle style;

        public SimpleStyle(@NonNull BeerStyle style) {
            this.style = get(style);
        }

        @Override
        public int getId() {
            return style.id();
        }

        @NonNull
        @Override
        public String getName() {
            return style.name();
        }

        @NonNull
        @Override
        public String getCode() {
            return style.name().substring(0, 2);
        }
    }

}
