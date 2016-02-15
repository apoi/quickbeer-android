package quickbeer.android.next.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.squareup.picasso.Transformation;

public class BottleLabelExtractor implements Transformation {

    private final int width;
    private final int height;

    public BottleLabelExtractor(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        Bitmap result = getLabel(source);
        source.recycle();

        return result;
    }

    @Override
    public String key() {
        return String.format("label-%d-%d", this.width, this.height);
    }

    private Bitmap getLabel(final Bitmap source) {
        // We'll want a square part from the middle, so get the max size
        final int side = (int) (Math.min(source.getWidth(), source.getHeight()) * 0.95f);

        Bitmap destination = Bitmap.createBitmap(side, side, source.getConfig());
        Rect destinationRect = new Rect(0, 0, destination.getWidth(), destination.getHeight());
        Canvas canvas = new Canvas(destination);

        // Guess the interesting part. On longer images it's probably lower
        // where the label is; on fatter images it's most likely full area.
        // Works quite well with cans (fat) and long-necked bottles (long) at least.
        float cutoffTop;
        float cutoffBottom;

        if (source.getHeight() > 2.0 * source.getWidth()) {
            cutoffTop = 0.450f; // Start just a bit before halfway to skip the bottle's neck
            cutoffBottom = 0.125f; // End cut just before the end; labels are usually almost to the bottom
        } else {
            cutoffTop = 0.220f; // We assume this is a can; small cuts from top & bottom
            cutoffBottom = 0.200f;
        }

        // How much to cut from both sides
        final int offSides = (int) (((float) source.getWidth() - side) / 2);

        // Divide vertical cut on the correct clip ratio
        final float offVerticalRatio = cutoffTop / cutoffBottom;
        final float offVertical = source.getHeight() - side;
        final int offTop = (int) (offVertical / (offVerticalRatio + 1) * offVerticalRatio);

        final Rect sourceRect = new Rect(offSides, offTop, side, side);
        canvas.drawBitmap(source, sourceRect, destinationRect, null);

        return destination;
    }
}
