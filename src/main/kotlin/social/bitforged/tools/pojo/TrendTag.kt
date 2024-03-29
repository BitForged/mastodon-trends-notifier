package social.bitforged.tools.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class TrendTag(
    @JsonProperty("id") override val id: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("url") override val url: String,
    @JsonProperty("history") val history: List<TrendHistory>,
    @JsonProperty("trendable") val trendable: Boolean,
    @JsonProperty("usable") val usable: Boolean,
    @JsonProperty("requires_review") override val requiresReview: Boolean,
    @JsonProperty("following") val following: Boolean,
    @JsonProperty("listable") val listable: Boolean,
): Trend(id, url, requiresReview)
