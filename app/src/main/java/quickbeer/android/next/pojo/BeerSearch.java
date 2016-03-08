package quickbeer.android.next.pojo;

import java.util.List;

public class BeerSearch {
    final private String search;
    final private List<Integer> items;

    public BeerSearch(final String search, final List<Integer> items) {
        this.search = search;
        this.items = items;
    }

    public String getSearch() {
        return search;
    }

    public List<Integer> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "BeerSearch{search=" + search
                + ", items='" + (items == null ? "null " : items.size())
                + "'}";
    }
}
