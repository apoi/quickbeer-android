package quickbeer.android.next.pojo;

import io.reark.reark.pojo.OverwritablePojo;

/**
 * Class to implement our specific json empty value definitions, to avoid overwriting
 * existing data with invalid values.
 */
public abstract class BasePojo<T extends OverwritablePojo> extends OverwritablePojo<T> {
    @Override
    protected boolean isEmpty(int value) {
        return value == 0;
    }

    @Override
    protected boolean isEmpty(long value) {
        return value == 0;
    }

    @Override
    protected boolean isEmpty(double value) {
        return value == 0;
    }

    @Override
    protected boolean isEmpty(float value) {
        return value == 0;
    }
}
