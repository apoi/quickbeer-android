package quickbeer.android.network

import quickbeer.android.domain.beer.network.BeerJson
import quickbeer.android.network.result.ApiResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface RateBeerApi {

    @FormUrlEncoded
    @POST("/Signin_r.asp")
    suspend fun login(
        @Field("username")
        username: String,

        @Field("pwd")
        password: String,

        @Field("saveinfo")
        saveinfo: String
    )

    @GET("/json/bff.asp")
    suspend fun getBeer(@Query("bd") id: Int): ApiResult<List<BeerJson>>

    @GET("/json/bff.asp")
    suspend fun search(@Query("bn") query: String): ApiResult<List<BeerJson>>

    @GET("/json/upc.asp")
    suspend fun barcode(@QueryMap params: Map<String, String>): ApiResult<List<BeerJson>>

    @GET("/json/tb.asp")
    suspend fun topBeers(@QueryMap params: Map<String, String>): ApiResult<List<BeerJson>>

    @GET("/json/style.asp")
    suspend fun beersInStyle(@QueryMap params: Map<String, String>): ApiResult<List<BeerJson>>

    /*
    @GET("/json/gr.asp")
    suspend fun getReviews(@QueryMap params: Map<String, String>): ApiResult<List<Review>>

    @GET("/json/revs.asp")
    suspend fun getUserReviews(@QueryMap params: Map<String, String>): ApiResult<List<Review>>
    */

    @GET("/json/bt.asp")
    suspend fun getTicks(@QueryMap params: Map<String, String>): ApiResult<List<BeerJson>>

    @GET("/json/bt.asp")
    suspend fun tickBeer(@QueryMap params: Map<String, String>)

    /*
    @GET("/json/bi.asp")
    suspend fun getBrewer(@QueryMap params: Map<String, String>): ApiResult<List<Brewer>>
    */

    @GET("/json/bw.asp")
    suspend fun getBrewerBeers(@QueryMap params: Map<String, String>): ApiResult<List<BeerJson>>
}
