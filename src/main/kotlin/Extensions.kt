
import org.slf4j.LoggerFactory
import social.bitforged.tools.pojo.Trend
import java.util.*

fun String.toTitleCase(): String {
    if (this.isEmpty()) {
        return this
    }
    val firstChar = this.substring(0, 1).toUpperCase(Locale.getDefault())
    val restOfTheString = this.substring(1).toLowerCase(Locale.getDefault())
    return firstChar + restOfTheString
}

fun getStaticLoggerForTrend(clazz: Class<out Trend>): org.slf4j.Logger {
    return LoggerFactory.getLogger(clazz)
}