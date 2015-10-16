package quickbeer.android.next.pojo;

import java.util.List;

/**
 * Created by antti on 17.10.2015.
 */
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
}
