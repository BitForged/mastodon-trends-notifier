package social.bitforged.tools.interfaces

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import social.bitforged.tools.pojo.TrendTag
import social.bitforged.tools.pojo.TrendLink

interface TrendsApi {
    @GET("admin/trends/tags?limit=20")
    fun getTrendingTags(): Call<List<TrendTag>>

    @POST("admin/trends/tags/{id}/approve")
    fun approveTag(@Path("id") tagId: String): Call<Void>

    @POST("admin/trends/tags/{id}/reject")
    fun rejectTag(@Path("id") tagId: String): Call<Void>

    @GET("admin/trends/links?limit=20")
    fun getTrendingLinks(): Call<List<TrendLink>>

    @POST("admin/trends/links/{id}/approve")
    fun approveLink(@Path("id") linkId: String): Call<Void>

    @POST("admin/trends/links/{id}/reject")
    fun rejectLink(@Path("id") linkId: String): Call<Void>
}