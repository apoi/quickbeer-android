package quickbeer.android.data.pojos;

import com.google.auto.value.AutoValue;

public class Session {

    private boolean ticksRequested;

    public boolean isTicksRequested() {
        return ticksRequested;
    }

    public void setTicksRequested(boolean ticksRequested) {
        this.ticksRequested = ticksRequested;
    }

}
