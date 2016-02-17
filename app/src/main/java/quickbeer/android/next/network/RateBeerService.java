package quickbeer.android.next.network;

import android.net.Uri;

import java.util.List;
import java.util.Map;

import quickbeer.android.next.pojo.Beer;
import quickbeer.android.next.pojo.Review;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by antti on 17.10.2015.
 */
public interface RateBeerService {
    static Uri BEER = Uri.parse("ratebeer/beer");
    static Uri SEARCH = Uri.parse("ratebeer/search");
    static Uri TOP50 = Uri.parse("ratebeer/top50");
    static Uri REVIEWS = Uri.parse("ratebeer/reviews");

    @GET("/json/bff.asp")
    Observable<List<Beer>> getBeer(@QueryMap Map<String, String> params);

    @GET("/json/bff.asp")
    Observable<List<Beer>> search(@QueryMap Map<String, String> params);

    @GET("/json/tb.asp")
    Observable<List<Beer>> searchTopBeers(@QueryMap Map<String, String> params);

    @GET("/json/gr.asp")
    Observable<List<Review>> getReviews(@QueryMap Map<String, String> params);
}
