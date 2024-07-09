package quickbeer.android.network

import okhttp3.ResponseBody
import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.domain.brewer.network.BrewerJson
import quickbeer.android.domain.rating.network.BeerRatingJson
import quickbeer.android.domain.rating.network.UserRatingJson
import quickbeer.android.domain.stylelist.network.StyleJson
import quickbeer.android.domain.user.network.RateCountJson
import quickbeer.android.network.result.ApiResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

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

    // RATINGS

    @GET("/json/gr.asp")
    suspend fun getRatings(
        @Query("bid") beerId: String,
        @Query("p") page: Int
    ): ApiResult<List<BeerRatingJson>>

    @GET("/json/revs.asp?m=BR&x=2&x2=2")
    suspend fun getUsersRatings(@Query("p") page: Int): ApiResult<List<UserRatingJson>>

    @FormUrlEncoded
    @POST("/saverating.asp")
    suspend fun postRating(
        @Field("beerId") beerId: Int,
        @Field("appearance") appearance: Int,
        @Field("aroma") aroma: Int,
        @Field("flavor") flavor: Int,
        @Field("palate") palate: Int,
        @Field("overall") overall: Int,
        @Field("comments", encoded = true) comments: String
    ): ApiResult<ResponseBody>

    @FormUrlEncoded
    @POST("/updaterating.asp")
    suspend fun updateRating(
        @Field("ratingId") ratingId: Int,
        @Field("beerId") beerId: Int,
        @Field("appearance") appearance: Int,
        @Field("aroma") aroma: Int,
        @Field("flavor") flavor: Int,
        @Field("palate") palate: Int,
        @Field("overall") overall: Int,
        @Field("comments", encoded = true) comments: String
    ): ApiResult<ResponseBody>

    @GET("/json/bt.asp?m=1")
    suspend fun getTicks(@Query("u") userId: String): ApiResult<List<BeerJson>>

    @GET("/json/bt.asp")
    suspend fun tickBeer(
        @Query("b") beerId: Int,
        @Query("u") userId: Int,
        @Query("m") mode: Int,
        @Query("l") tick: Int?
    ): ApiResult<ResponseBody>

    // USER DETAILS

    @GET("/json/rc.asp")
    suspend fun getUserRateCount(@Query("uid") userId: String): ApiResult<List<RateCountJson>>
}
