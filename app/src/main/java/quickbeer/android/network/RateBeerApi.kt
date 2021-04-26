package quickbeer.android.network

import okhttp3.ResponseBody
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.brewer.network.BrewerJson
import quickbeer.android.domain.review.network.ReviewJson
import quickbeer.android.domain.stylelist.network.StyleJson
import quickbeer.android.network.result.ApiResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface RateBeerApi {

    // LOGIN

    @FormUrlEncoded
    @POST("/Signin_r.asp")
    suspend fun login(
        @Field("username") username: String,
        @Field("pwd") password: String,
        @Field("saveinfo") saveinfo: String
    ): ApiResult<ResponseBody>

    // BEERS

    /**
     * Optional parameters applicable to certain calls:
     *   SID = style id
     *   CID = country id
     *   RC = rate count
     *   VG = average ratings
     */

    @GET("/json/bff.asp?sid=1&cid=1&rc=1&vg=1")
    suspend fun beer(@Query("bd") beerId: Int): ApiResult<List<BeerJson>>

    @GET("/json/bff.asp?rc=1")
    suspend fun beerSearch(@Query("bn") query: String): ApiResult<List<BeerJson>>

    @GET("/json/upc.asp")
    suspend fun barcodeSearch(@Query("upc") query: String): ApiResult<List<BeerJson>>

    @GET("/json/tb.asp?m=top50")
    suspend fun topBeers(): ApiResult<List<BeerJson>>

    // BREWERS

    @GET("/json/bi.asp")
    suspend fun brewer(@Query("b") brewerId: Int): ApiResult<List<BrewerJson>>

    @GET("/json/bss.asp")
    suspend fun brewerSearch(@Query("bn") query: String): ApiResult<List<BrewerJson>>

    @GET("/json/bw.asp")
    suspend fun brewerBeers(@Query("b") brewerId: String): ApiResult<List<BeerJson>>

    // STYLES

    @GET("/json/styles.asp")
    suspend fun styles(): ApiResult<List<StyleJson>>

    @GET("/json/style.asp")
    suspend fun beersInStyle(@Query("s") styleId: String): ApiResult<List<BeerJson>>

    // COUNTRIES

    @GET("/json/tb.asp?m=country")
    suspend fun beersInCountry(@Query("c") countryId: String): ApiResult<List<BeerJson>>

    // REVIEWS

    @GET("/json/gr.asp")
    suspend fun getReviews(
        @Query("bid") beerId: String,
        @Query("p") page: Int
    ): ApiResult<List<ReviewJson>>

    /*
    @GET("/json/revs.asp")
    suspend fun getUserReviews(@QueryMap params: Map<String, String>): ApiResult<List<Review>>
    */

    @GET("/json/bt.asp")
    suspend fun getTicks(@QueryMap params: Map<String, String>): ApiResult<List<BeerJson>>

    @GET("/json/bt.asp")
    suspend fun tickBeer(@QueryMap params: Map<String, String>)
}
