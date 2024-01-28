package social.bitforged.tools.etc

import it.sauronsoftware.cron4j.Scheduler
import net.system86.jist.lib.interactions.impl.button.ButtonsInteraction
import net.system86.jist.lib.logging.Loggable
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import social.bitforged.tools.Plugin
import social.bitforged.tools.pojo.Tag
import social.bitforged.tools.services.TrendService
import java.awt.Color
import java.util.*

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
    }

    private fun findPendingTrendsAndNotify(): Runnable {
        val instanceName = ConfigManager.getInstance().getConfig().instanceName
        val notificationChannelId = ConfigManager.getInstance().getConfig().trendNotificationChannelId
        return Runnable {
            // Find all pending trends
            TrendService().getTrendingTags()?.forEach { tag ->
                logger.debug("Found tag with ID ${tag.id} ('${tag.name}'), requires review: ${tag.requiresReview}")
                if (!handledTrendIds.contains(tag.id) && tag.requiresReview) {
                    // Build an embed to send to the channel
                    val embed = EmbedBuilder()
                        .setColor(Color.YELLOW)
                        .setTitle("A New Trending Tag Requires Review!")
                        .setDescription("A new tag is trending on $instanceName and needs approval! " +
                                "Please review the contents over [here](${tag.url}), then approve or deny it.")
                        .addField("Tag Name", tag.name, false)
                        .addField("Uses Today", tag.history[0].uses.toString(), true)

                    // Build an interaction for the target channel
                    val builder = ButtonsInteraction.builder()
                    builder.addEmbed(embed)
                    builder.addButton(Button.success("approve", "Approve", "✅"))
                    builder.addButton(Button.danger("deny", "Reject", "\uD83D\uDDD1\uFE0F")) // "Trash can" emoji
                    builder.accept { event -> // Creates the listener for the buttons
                        if (event.interaction.customId.contains("approve")) {
                            val updatedEmbed =
                                createUpdatedTrendEmbed(TrendApprovalStatus.APPROVED, tag, event.interaction.user.name)
                            val res = trendService.approveTag(tag.id.toString()).execute()
                            logger.debug("Tag ${tag.id} Trend Approval Response: ${res.code()}")
                            event.message.createUpdater().removeAllComponents().setEmbed(updatedEmbed).applyChanges()
                                .join()
                        } else if (event.interaction.customId.contains("deny")) {
                            val updatedEmbed =
                                createUpdatedTrendEmbed(TrendApprovalStatus.DENIED, tag, event.interaction.user.name)
                            val res = trendService.rejectTag(tag.id.toString()).execute()
                            logger.debug("Tag ${tag.id} Trend Rejection Response: ${res.code()}")
                            event.message.createUpdater().removeAllComponents().setEmbed(updatedEmbed).applyChanges()
                                .join()
                        } else {
                            event.interaction.acknowledge()
                                .join() // This prevents the interaction from timing out on Discord
                        }
                    }
                    val jistInteraction = builder.build().toJistInteraction(plugin)
                    jistInteraction.applyTo(
                        // Send the interaction to the target channel
                        plugin.masterApi.getChannelById(notificationChannelId).get().asTextChannel().get()
                    )

                    handledTrendIds.add(tag.id) // Remember the tag ID, so we don't send it again on the next run

                } else {
                    logger.debug("Tag with ID ${tag.id} doesn't require review, ignoring!")
                }
            }
        }
    }

    /**
     * Updates an embed after the approval/denial of a trending tag.
     */
    private fun createUpdatedTrendEmbed(status: TrendApprovalStatus, tag: Tag, actor: String): EmbedBuilder {
        val titleApprovalIndicator = "✅"
        val titleDenialIndicator = "❌"
        val title = when (status) {
            TrendApprovalStatus.APPROVED -> "$titleApprovalIndicator Trend Approved"
            TrendApprovalStatus.DENIED -> "$titleDenialIndicator Trend Denied"
        }
        val actorTagline = when (status) {
            TrendApprovalStatus.APPROVED -> "Approved by"
            TrendApprovalStatus.DENIED -> "Denied by"
        }
        val embed = EmbedBuilder()
            .setTitle(title)
            .setDescription("A trending tag has been ${status.name.lowercase(Locale.getDefault())}! " +
                    "The usage of the tag can be found [here](${tag.url}).")
            .addField("Tag", tag.name, false)
            .addField(actorTagline, actor, true)
            .addField("Status", status.status, true)

        if (status == TrendApprovalStatus.APPROVED) {
            embed.setColor(Color.GREEN)
        } else {
            embed.setColor(Color.RED)
        }

        return embed
    }

    enum class TrendApprovalStatus(val status: String) {

        APPROVED("Approved"),
        DENIED("Rejected")

    }
}