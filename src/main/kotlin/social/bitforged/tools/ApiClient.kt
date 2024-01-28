package social.bitforged.tools

import net.system86.jist.lib.logging.Loggable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import social.bitforged.tools.etc.ConfigManager

object ApiClient: Loggable {

    private val BASE_URL = "${ConfigManager.getInstance().getConfig().apiBaseUrl}/api/v1/"

    private val okHttpClient: OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(ApiRequestInterceptor)
        .build()

    fun getClient(): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()

}