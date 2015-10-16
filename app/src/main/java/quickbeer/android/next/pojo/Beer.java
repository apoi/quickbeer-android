package quickbeer.android.next.pojo;

import android.support.annotation.NonNull;

import quickbeer.android.next.utils.Preconditions;

/**
 * Created by antti on 17.10.2015.
 */
public class Beer {
    final private int id;

    @NonNull
    final private String name;

    public Beer(int id, @NonNull String name) {
        Preconditions.checkNotNull(name, "Beer name can't be null");

        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Beer{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Beer)) {
            return false;
        }

        Beer that = (Beer) o;

        if (id != that.id) {
            return false;
        } else if (!name.equals(that.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }
}

