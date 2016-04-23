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

import quickbeer.android.next.utils.StringUtils;

public class UserSettings extends BasePojo<UserSettings> {
    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("isLogged")
    private boolean isLogged;

    @NonNull
    @Override
    protected Class<UserSettings> getTypeParameterClass() {
        return UserSettings.class;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setIsLogged(boolean logged) {
        isLogged = logged;
    }

    public boolean credentialEqual(String username, String password) {
        return StringUtils.equals(username, this.username)
                && StringUtils.equals(password, this.password);
    }

    @Override
    public String toString() {
        return "UserSettings{" +
                "username='" + username + '\'' +
                ", isLogged=" + isLogged +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserSettings that = (UserSettings) o;

        if (isLogged != that.isLogged) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;

        return password != null ? password.equals(that.password) : that.password == null;

    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (isLogged ? 1 : 0);
        return result;
    }
}
