package quickbeer.android.next.network.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reark.reark.utils.Log;

public class DateDeserializer implements JsonDeserializer<Date>, JsonSerializer<Date> {
    private static final String TAG = DateDeserializer.class.getSimpleName();
    private static final DateFormat ISO_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ROOT);
    private static final DateFormat US_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss aa", Locale.ROOT);

    @Override
    public JsonElement serialize(Date date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(ISO_FORMAT.format(date));
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        String date = json.getAsString();

        try {
            if (date.contains("T")) {
                return ISO_FORMAT.parse(date);
            } else {
                return US_FORMAT.parse(date);
            }
        } catch (ParseException e) {
            Log.e(TAG, "error parsing " + date, e);
            return new Date();
        }
    }
}
