package social.bitforged.tools.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class Tag(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") val name: String,
    @JsonProperty("url") val url: String,
    @JsonProperty("history") val history: List<TrendHistory>,
    @JsonProperty("trendable") val trendable: Boolean,
    @JsonProperty("usable") val usable: Boolean,
    @JsonProperty("requires_review") val requiresReview: Boolean,
    @JsonProperty("following") val following: Boolean,
    @JsonProperty("listable") val listable: Boolean,
)
