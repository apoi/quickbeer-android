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
public abstract class User {

    @SerializedName("id")
    public abstract Integer id();

    @SerializedName("username")
    public abstract String username();

    @SerializedName("password")
    public abstract String password();

    // Accessors

    public boolean credentialsEqual(@Nullable final String username, @Nullable final String password) {
        return StringUtils.equals(username, username())
                && StringUtils.equals(password, password());
    }

    // Plumbing

    @SuppressWarnings("ClassReferencesSubclass")
    @AutoValue.Builder
    public abstract static class Builder extends OverwritableBuilder<AutoValue_User.Builder> {

        public abstract Builder id(@Nullable final Integer id);

        public abstract Builder username(@Nullable final String username);

        public abstract Builder password(@Nullable final String password);

        public abstract User build();

        @NonNull
        @Override
        protected Class<AutoValue_User.Builder> getTypeParameterClass() {
            return AutoValue_User.Builder.class;
        }
    }

    @NonNull
    public static TypeAdapter<User> typeAdapter(@NonNull final Gson gson) {
        return new AutoValue_User.GsonTypeAdapter(get(gson));
    }

    @NonNull
    public static Builder builder() {
        return new AutoValue_User.Builder();
    }

    @NonNull
    public static Builder builder(@NonNull final User user) {
        return new AutoValue_User.Builder(user);
    }

    @NonNull
    public static User merge(@NonNull final User v1, @NonNull final User v2) {
        AutoValue_User.Builder builder1 = new AutoValue_User.Builder(get(v1));
        AutoValue_User.Builder builder2 = new AutoValue_User.Builder(get(v2));

        return builder1.overwrite(builder2).build();
    }
}
