package quickbeer.android.next.network;

import java.util.Map;

import quickbeer.android.next.network.results.BeerSearchResults;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by antti on 17.10.2015.
 */
public interface RateBeerService {
    @GET("/json/bff.asp")
    Observable<BeerSearchResults> search(@QueryMap Map<String, String> search);

    @GET("/json/tb.asp?m=top50")
    Observable<BeerSearchResults> searchTopBeers();
}
