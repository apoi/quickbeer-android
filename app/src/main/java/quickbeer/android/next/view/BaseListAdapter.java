package quickbeer.android.next.view;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;

import com.yqritc.recyclerviewflexibledivider.FlexibleDividerDecoration;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * Created by antti on 1.11.2015.
 */
public abstract class BaseListAdapter<T extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<T>
        implements FlexibleDividerDecoration.PaintProvider,
        HorizontalDividerItemDecoration.MarginProvider {

    @Override
    public Paint dividerPaint(int position, RecyclerView parent) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1);
        paint.setAlpha(50);
        return paint;
    }

    @Override
    public int dividerLeftMargin(int position, RecyclerView parent) {
        return 50;
    }

    @Override
    public int dividerRightMargin(int position, RecyclerView parent) {
        return 50;
    }
}
