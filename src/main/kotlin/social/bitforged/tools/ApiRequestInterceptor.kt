package social.bitforged.tools

import net.system86.jist.lib.logging.Loggable
import okhttp3.Interceptor
import okhttp3.Response
import social.bitforged.tools.etc.ConfigManager

object ApiRequestInterceptor : Interceptor, Loggable {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        logger.debug("Outgoing request to {}", request.url())
        val modifiedRequest = request.newBuilder()
            .addHeader("User-Agent", "BitForged Social Tools")
            .addHeader("Authorization", "Bearer ${ConfigManager.getInstance().getConfig().apiToken}")
            .build()
        return chain.proceed(modifiedRequest)
    }
}