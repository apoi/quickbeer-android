package quickbeer.android.next.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import quickbeer.android.next.R;
import quickbeer.android.next.pojo.Beer;

/**
 * Created by antti on 25.10.2015.
 */
public class BeerListAdapter extends RecyclerView.Adapter<BeerListAdapter.ViewHolder> {
    private final List<Beer> beers = new ArrayList<>();
    private View.OnClickListener onClickListener;

    public BeerListAdapter(List<Beer> beers) {
        this.beers.addAll(beers);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public Beer getItem(int position) {
        return beers.get(position);
    }

    @Override
    public BeerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.beer_list_item, parent, false);
        return new ViewHolder(v, onClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameTextView.setText(beers.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return beers.size();
    }

    public void set(List<Beer> gitHubRepositories) {
        this.beers.clear();
        this.beers.addAll(gitHubRepositories);

        notifyDataSetChanged();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;

        public ViewHolder(View view, View.OnClickListener onClickListener) {
            super(view);
            nameTextView = (TextView) view.findViewById(R.id.beer_name);
            view.setOnClickListener(onClickListener);
        }
    }
}
