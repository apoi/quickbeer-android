package quickbeer.android.next.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import quickbeer.android.next.R;
import quickbeer.android.next.adapters.MenuListAdapter;
import quickbeer.android.next.views.listitems.MenuListItem;

public class MainView extends BeerListView {
    private static final String TAG = MainView.class.getSimpleName();

    private List<MenuListItem> menuListItems;
    private RecyclerView menuListView;
    private MenuListAdapter menuListAdapter;

    public MainView(Context context) {
        super(context);
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        menuListItems = new ArrayList<>();
        menuListItems.add(new MenuListItem("search beers", R.drawable.score_unrated));
        menuListItems.add(new MenuListItem("best beers", R.drawable.score_unrated));
        menuListItems.add(new MenuListItem("brewers", R.drawable.score_unrated));
        menuListItems.add(new MenuListItem("scan barcode", R.drawable.score_unrated));

        menuListAdapter = new MenuListAdapter(menuListItems);
        menuListView = (RecyclerView) findViewById(R.id.menu_list_view);
        menuListView.setHasFixedSize(true);
        menuListView.setLayoutManager(new LinearLayoutManager(getContext()));
        menuListView.setAdapter(menuListAdapter);

        // Set enough margin for the menu to be visible
        final int menuItemHeight = (int) getResources().getDimension(R.dimen.menu_item_height);
        final int headerItemHeight = (int) getResources().getDimension(R.dimen.header_item_height);
        getAdapter().setHeaderHeight(menuListItems.size() * menuItemHeight + headerItemHeight);

        // Redirect unhandled click events to menu
        getListView().setOnTouchListener((view, event) -> {
            menuListView.dispatchTouchEvent(event);
            return false;
        });
    }
}
