package quickbeer.android.next.pojo;

import java.util.List;

public class ReviewList {
    final private int beerId;
    final private List<Integer> items;

    public ReviewList(final int beerId, final List<Integer> items) {
        this.beerId = beerId;
        this.items = items;
    }

    public int getBeerId() {
        return beerId;
    }

    public List<Integer> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "ReviewList{beerId=" + beerId
                + ", items='" + (items == null ? "null " : items.size())
                + "'}";
    }
}
