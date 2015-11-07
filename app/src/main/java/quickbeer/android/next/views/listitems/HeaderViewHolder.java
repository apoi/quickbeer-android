package quickbeer.android.next.views.listitems;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import quickbeer.android.next.R;

/**
 * Created by antti on 7.11.2015.
 */
public class HeaderViewHolder extends RecyclerView.ViewHolder {

    private TextView header;

    public HeaderViewHolder(View view) {
        super(view);

        this.header = (TextView) view.findViewById(R.id.header_text);

        // Prevent click-through on the header area
        view.findViewById(R.id.header_area).setOnTouchListener((v, event) -> {
            return true;
        });
    }

    public void setHeader(String value) {
        header.setText(value);
    }
}
