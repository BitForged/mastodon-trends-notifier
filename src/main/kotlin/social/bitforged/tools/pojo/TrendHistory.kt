package social.bitforged.tools.pojo

import com.fasterxml.jackson.annotation.JsonProperty

data class TrendHistory(
    @JsonProperty("day") val day: Long,
    @JsonProperty("uses") val uses: Int,
    @JsonProperty("accounts") val accounts: Int
)
