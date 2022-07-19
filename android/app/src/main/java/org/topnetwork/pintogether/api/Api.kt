package org.topnetwork.pintogether.api
import com.topnetwork.net.model.ApiResponse
import com.topnetwork.net.network.RetrofitManager
import io.reactivex.Observable
import okhttp3.RequestBody
import org.topnetwork.pintogether.model.NearBy
import retrofit2.http.*

interface Api {
    companion object {
        val instance: Api
            get() = RetrofitManager.instance.getApi(Api::class.java)
    }

    //创建nft
    @POST("/v1/app/gift")
    fun createGift(@Body requestBody: RequestBody): Observable<ApiResponse<String>>

    //附近
    @POST("/v1/app/nearby")
    fun nearby(@Body requestBody: RequestBody): Observable<ApiResponse<List<NearBy>>>


    @POST("/v1/app/gift/page")
    fun giftPage(@Body requestBody: RequestBody): Observable<ApiResponse<List<NearBy>>>

    //我得到的
    @POST("/v1/app/receive/page")
    fun receivePage(@Body requestBody: RequestBody): Observable<ApiResponse<List<NearBy>>>

    //领取
    @POST("/v1/app/receive")
    fun receive(@Body requestBody: RequestBody): Observable<ApiResponse<String>>

    @GET("/v1/app/check/{account}/{giftId}")
    fun check(@Path("account") account:String,@Path("giftId") giftId:String): Observable<ApiResponse<String>>
}


