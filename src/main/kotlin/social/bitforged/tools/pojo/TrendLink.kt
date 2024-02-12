package social.bitforged.tools.pojo

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class TrendLink(
    override val id: Int,
    override val url: String,
    val title: String,
    val description: String,
    val language: String,
    val type: String,
    @JsonProperty("author_name") val authorName: String,
    @JsonProperty("author_url") val authorUrl: String,
    @JsonProperty("provider_name") val providerName: String,
    @JsonProperty("provider_url") val providerUrl: String,
    val width: Int,
    val height: Int,
    val html: String,
    val image: String,
    @JsonProperty("image_description") val imageDescription: String,
    @JsonProperty("embed_url") val embedUrl: String,
    val history: List<TrendHistory>,
    @JsonProperty("requires_review") override val requiresReview: Boolean,
): Trend(id, url, requiresReview)
