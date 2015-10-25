package quickbeer.android.next.network;

import java.util.List;
import java.util.Map;

import quickbeer.android.next.pojo.Beer;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by antti on 17.10.2015.
 */
public interface RateBeerService {
    @GET("/json/bff.asp")
    Observable<Beer> getBeer(@QueryMap Map<String, String> params);

    @GET("/json/bff.asp")
    Observable<List<Beer>> search(@QueryMap Map<String, String> params);

    @GET("/json/tb.asp")
    Observable<List<Beer>> searchTopBeers(@QueryMap Map<String, String> params);
}
