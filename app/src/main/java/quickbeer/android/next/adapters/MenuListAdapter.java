package quickbeer.android.next.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import quickbeer.android.next.R;
import quickbeer.android.next.adapters.BaseListAdapter;
import quickbeer.android.next.views.listitems.ItemType;
import quickbeer.android.next.views.listitems.MenuListItem;

/**
 * Created by antti on 8.11.2015.
 */
public class MenuListAdapter extends BaseListAdapter<MenuListItem.MenuViewHolder> {
    private final List<MenuListItem> items;

    public MenuListAdapter(List<MenuListItem> items) {
        this.items = items;
    }

    @Override
    public MenuListItem.MenuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_list_item, parent, false);
        return new MenuListItem.MenuViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MenuListItem.MenuViewHolder holder, int position) {
        MenuListItem item = items.get(position);
        holder.textView.setText(item.getText());
        holder.iconView.setImageResource(item.getIcon());
    }

    @Override
    public int getItemViewType(int position) {
        return ItemType.MENU.ordinal();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
