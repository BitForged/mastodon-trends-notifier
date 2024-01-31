package social.bitforged.tools

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
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

    fun getClient(): Retrofit {
        val mapper = jacksonObjectMapper().apply {
            registerModule(kotlinModule())
            configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .build()
    }

}