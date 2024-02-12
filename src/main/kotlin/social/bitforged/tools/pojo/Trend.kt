package social.bitforged.tools.pojo

import com.fasterxml.jackson.annotation.JsonProperty

open class Trend(
    open val id: Int,
    open val url: String,
    @JsonProperty("requires_review") open val requiresReview: Boolean
)
