package quickbeer.android.next.utils;

public class StringUtils {
    private StringUtils() {}

    public static String value(String primary, String secondary) {
        return primary != null && !primary.isEmpty() ? primary : secondary;
    }
}
