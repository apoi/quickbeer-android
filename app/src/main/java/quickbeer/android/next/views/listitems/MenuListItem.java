package quickbeer.android.next.views.listitems;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.reark.reark.utils.Log;
import quickbeer.android.next.R;

/**
 * Created by antti on 8.11.2015.
 */
public class MenuListItem {
    private String text;
    private int icon;

    public MenuListItem(String text, int icon) {
        this.text = text;
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public int getIcon() {
        return icon;
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView iconView;

        public MenuViewHolder(View view) {
            super(view);

            textView = (TextView) view.findViewById(R.id.menu_text);
            iconView = (ImageView) view.findViewById(R.id.menu_icon);

            view.setOnClickListener(v -> {
                Log.e("FOO", "CLICK!");
            });
        }
    }
}
