package social.bitforged.tools.interfaces

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import social.bitforged.tools.pojo.Tag

interface TrendsApi {
    @GET("admin/trends/tags?limit=20")
    fun getTrendingTags(): Call<List<Tag>>

    @POST("admin/trends/tags/{id}/approve")
    fun approveTag(@Path("id") tagId: String): Call<Void>

    @POST("admin/trends/tags/{id}/reject")
    fun rejectTag(@Path("id") tagId: String): Call<Void>
}