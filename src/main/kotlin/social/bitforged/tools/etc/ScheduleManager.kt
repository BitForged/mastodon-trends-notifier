package social.bitforged.tools.etc

import it.sauronsoftware.cron4j.Scheduler
import net.system86.jist.lib.logging.Loggable
import social.bitforged.tools.Plugin
import social.bitforged.tools.postTrendToDiscord
import social.bitforged.tools.services.TrendService

class ScheduleManager(private val plugin: Plugin) : Loggable {
    private val manager = Scheduler()
    private val handledTrendIds = ArrayList<Int>()
    private val trendService = TrendService()

    init {
        logger.info("Initializing scheduler...")
        val cronString = ConfigManager.getInstance().getConfig().cronString
        logger.info("Using scheduler/cron string: $cronString")
        manager.schedule(cronString, findPendingTrendsAndNotify())
        manager.start()
        logger.info("Initialized scheduler!")
        if(ConfigManager.getInstance().getConfig().runOnStart) {
            logger.info("Running initial trend check...")
            findPendingTrendsAndNotify().run()
        } else {
            logger.info("Skipping initial trend check, as per configuration")
        }
    }

    private fun findPendingTrendsAndNotify(): Runnable {
        return Runnable {
            // Find all pending trends
            TrendService().getTrendingTags()?.forEach { tag ->
                logger.debug("Found trending tag with ID ${tag.id} ('${tag.name}'), requires review: ${tag.requiresReview}")
                if (!handledTrendIds.contains(tag.id) && tag.requiresReview) {
                    postTrendToDiscord(tag, plugin)
                    handledTrendIds.add(tag.id) // Remember the tag ID, so we don't send it again on the next run
                } else {
                    logger.debug("Tag with ID ${tag.id} doesn't require review, ignoring!")
                }
            }

            TrendService().getTrendingLinks()?.forEach { tag ->
                logger.debug("Found trending link with ID ${tag.id} ('${tag.title}'), requires review: ${tag.requiresReview}")
                if(!handledTrendIds.contains(tag.id) && tag.requiresReview) {
                    postTrendToDiscord(tag, plugin)
                    handledTrendIds.add(tag.id)
                } else {
                    logger.debug("Link with ID ${tag.id} doesn't require review, ignoring!")
                }
            }
        }
    }

}