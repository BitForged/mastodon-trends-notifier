package social.bitforged.tools.services

import social.bitforged.tools.ApiClient
import social.bitforged.tools.interfaces.TrendsApi

class TrendService {
    private val apiClient = ApiClient.getClient()
    private val trendsApi = apiClient.create(TrendsApi::class.java)

    fun getTrendingTags() = trendsApi.getTrendingTags().execute().body()

    fun approveTag(tagId: String) = trendsApi.approveTag(tagId)

    fun rejectTag(tagId: String) = trendsApi.rejectTag(tagId)

}