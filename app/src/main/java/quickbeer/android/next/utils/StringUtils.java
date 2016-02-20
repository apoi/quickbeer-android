package quickbeer.android.next.utils;

public class StringUtils {
    private StringUtils() {}

    public static boolean hasValue(String value) {
        return value != null && !value.isEmpty();
    }

    public static String value(String primary, String secondary) {
        return hasValue(primary) ? primary : secondary;
    }
}
