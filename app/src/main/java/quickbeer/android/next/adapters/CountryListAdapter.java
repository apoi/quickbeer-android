package quickbeer.android.next.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import quickbeer.android.next.R;
import quickbeer.android.next.pojo.Country;
import quickbeer.android.next.views.listitems.CountryListItem;

public class CountryListAdapter extends BaseListAdapter<CountryListItem.ViewHolder> {
    private final List<Country> countries;

    public CountryListAdapter(Collection<Country> countries) {
        this.countries = new ArrayList<>(countries);
        Collections.sort(this.countries);
    }

    @Override
    public CountryListItem.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_list_item, parent, false);
        return new CountryListItem.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CountryListItem.ViewHolder holder, int position) {
        holder.setItem(countries.get(position));
    }

    @Override
    public int getItemCount() {
        return countries.size();
    }
}
