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
package quickbeer.android.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchAdapter extends BaseAdapter implements Filterable {

    @NonNull
    private final List<String> sourceList = new ArrayList<>(10);

    @NonNull
    private final List<String> adapterList = new ArrayList<>(10);

    @Nullable
    private Drawable suggestionIcon;

    @NonNull
    private final LayoutInflater inflater;

    public SearchAdapter(@NonNull final Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void updateSourceList(@NonNull final List<String> list) {
        sourceList.clear();
        sourceList.addAll(list);
    }

    @Override
    public int getCount() {
        return adapterList.size();
    }

    @Override
    public String getItem(int position) {
        return adapterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence filter) {
                FilterResults results = new FilterResults();
                Set<String> filtered = new HashSet<>();

                if (!TextUtils.isEmpty(filter)) {
                    List<String> filters = Arrays.asList(filter.toString().split(" "));
                    for (String value : sourceList) {
                        if (containsFilters(value, filters)) {
                            filtered.add(value);
                        }
                    }
                }

                results.values = new ArrayList<>(filtered);
                results.count = filtered.size();
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence filter, FilterResults results) {
                if (results.count > 0) {
                    adapterList.clear();
                    adapterList.addAll((Collection<String>) results.values);
                    notifyDataSetChanged();
                }
            }
        };
    }

    private static boolean containsFilters(String value, List<String> filters) {
        for (String filter : filters) {
            if (!value.contains(filter)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        MyViewHolder mViewHolder;
        View convertView;

        if (view != null) {
            convertView = view;
            mViewHolder = (MyViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(com.miguelcatalan.materialsearchview.R.layout.suggest_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        }

        String currentListData = getItem(position);
        mViewHolder.textView.setText(currentListData);

        return convertView;
    }

    private class MyViewHolder {
        final TextView textView;
        ImageView imageView;

        MyViewHolder(View convertView) {
            textView = (TextView) convertView.findViewById(com.miguelcatalan.materialsearchview.R.id.suggestion_text);
            if (suggestionIcon != null) {
                imageView = (ImageView) convertView.findViewById(com.miguelcatalan.materialsearchview.R.id.suggestion_icon);
                imageView.setImageDrawable(suggestionIcon);
            }
        }
    }
}
