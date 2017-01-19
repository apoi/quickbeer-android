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
package quickbeer.android.data.pojos;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import quickbeer.android.data.pojos.base.OverwritableBuilder;
import quickbeer.android.utils.StringUtils;

import static io.reark.reark.utils.Preconditions.get;

@AutoValue
public abstract class UserSettings {

    @SerializedName("username")
    public abstract String username();

    @SerializedName("password")
    public abstract String password();

    @SerializedName("userId")
    public abstract String userId();

    @NonNull
    @SerializedName("isLogged")
    public abstract Boolean isLogged();

    // Accessors

    public boolean credentialsEqual(@Nullable final String username, @Nullable final String password) {
        return StringUtils.equals(username, username())
                && StringUtils.equals(password, password());
    }

    // Plumbing

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends OverwritableBuilder<AutoValue_UserSettings.Builder> {

        public abstract Builder username(@Nullable final String username);

        public abstract Builder password(@Nullable final String password);

        public abstract Builder userId(@Nullable final String userId);

        public abstract Builder isLogged(@NonNull final Boolean isLogged);

        public abstract UserSettings build();

        @NonNull
        @Override
        protected Class<AutoValue_UserSettings.Builder> getTypeParameterClass() {
            return AutoValue_UserSettings.Builder.class;
        }
    }

    @NonNull
    public static TypeAdapter<UserSettings> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_UserSettings.GsonTypeAdapter(get(gson));
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_UserSettings.Builder();
    }

    @NonNull
    public static Builder builder(@NonNull final UserSettings userSettings) {
        return new AutoValue_UserSettings.Builder(userSettings);
    }

    @NonNull
    public static UserSettings merge(@NonNull final UserSettings v1, @NonNull final UserSettings v2) {
        AutoValue_UserSettings.Builder builder1 = new AutoValue_UserSettings.Builder(get(v1));
        AutoValue_UserSettings.Builder builder2 = new AutoValue_UserSettings.Builder(get(v2));

        return builder1.overwrite(builder2).build();
    }
}
