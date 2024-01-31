package social.bitforged.tools

import getStaticLoggerForTrend
import net.system86.jist.lib.interactions.impl.button.ButtonsInteraction
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import social.bitforged.tools.enums.TrendApprovalStatus
import social.bitforged.tools.enums.TrendType
import social.bitforged.tools.etc.ConfigManager
import social.bitforged.tools.pojo.Trend
import social.bitforged.tools.pojo.TrendLink
import social.bitforged.tools.pojo.TrendTag
import social.bitforged.tools.services.TrendService
import toTitleCase
import java.awt.Color

fun postTrendToDiscord(trend: Trend, plugin: Plugin) {
    val trendType: TrendType
    val embed = EmbedBuilder()
    embed.setColor(Color.YELLOW)
    if(trend is TrendTag) {
        trendType = TrendType.TAG
        val trendTypeTitle = trendType.name.toTitleCase()
        embed
            .setTitle("A new trending $trendTypeTitle has been detected!")
            .setDescription("A new trending $trendTypeTitle has been detected on ${ConfigManager.getInstance().getConfig().instanceName}! " +
                    "Please review the contents over [here](${trend.url}), then approve or deny it.")
            .addField("Tag Name", trend.name, false)
            .addField("Uses Today", trend.history[0].uses.toString(), true)
    } else if(trend is TrendLink) {
        trendType = TrendType.LINK
        val trendTypeTitle = trendType.name.toTitleCase()

        embed
            .setTitle("A new trending $trendTypeTitle has been detected!")
            .setDescription("A new trending $trendTypeTitle has been detected on ${ConfigManager.getInstance().getConfig().instanceName}! " +
                    "Please review the contents over [here](${trend.url}), then approve or deny it.")
            .addField("Link Title", trend.title, false)
            .addField("Uses Today", trend.history[0].uses.toString(), true)
            .setImage(trend.image)
    } else {
        getStaticLoggerForTrend(trend::class.java).error("Unknown trend type: ${trend.javaClass.name}")
        error("Unknown trend type: ${trend.javaClass.name}")
    }


    // Build an interaction for the target channel
    val builder = ButtonsInteraction.builder()
    builder.addEmbed(embed)
    builder.addButton(Button.success("approve", "Approve", "✅"))
    builder.addButton(Button.danger("deny", "Reject", "\uD83D\uDDD1\uFE0F")) // "Trash can" emoji
    builder.accept { event -> // Creates the listener for the buttons
        if (event.interaction.customId.contains("approve")) {
            val updatedEmbed =
                createUpdatedTrendEmbed(trend, trendType, TrendApprovalStatus.APPROVED, event.interaction.user.name)
            sendApprovalStatusToMastodon(trend, TrendApprovalStatus.APPROVED)
            event.message.createUpdater().removeAllComponents().setEmbed(updatedEmbed).applyChanges()
                .join()
        } else if (event.interaction.customId.contains("deny")) {
            val updatedEmbed =
                createUpdatedTrendEmbed(trend, trendType, TrendApprovalStatus.DENIED, event.interaction.user.name)
            sendApprovalStatusToMastodon(trend, TrendApprovalStatus.DENIED)
            event.message.createUpdater().removeAllComponents().setEmbed(updatedEmbed).applyChanges()
                .join()
        } else {
            event.interaction.acknowledge()
                .join() // This prevents the interaction from timing out on Discord
        }
    }
    val jistInteraction = builder.build().toJistInteraction(plugin)
    val notificationChannelId = ConfigManager.getInstance().getConfig().trendNotificationChannelId
    jistInteraction.applyTo(
        // Send the interaction to the target channel
        plugin.masterApi.getChannelById(notificationChannelId).get().asTextChannel().get()
    )
}

private fun createUpdatedTrendEmbed(trend: Trend, type: TrendType, status: TrendApprovalStatus, actor: String): EmbedBuilder {
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

    embed.setTitle(title)
    embed.setDescription("A trending ${type.name.toTitleCase()} has been ${status.name.lowercase()}!")

    when (status) {
        TrendApprovalStatus.APPROVED -> {
            embed.setColor(Color.GREEN)

        }
        TrendApprovalStatus.DENIED -> {
            embed.setColor(Color.RED)
        }
    }

    if(trend is TrendTag) {
        embed.addField("Tag Name", trend.name, false)
    } else if(trend is TrendLink) {
        embed.addField("Link Title", trend.title, false)
    }

    embed.addField(actorTagline, actor, true)
    embed.addField("Status", status.name.toTitleCase())

    return embed
}

private fun sendApprovalStatusToMastodon(trend: Trend, status: TrendApprovalStatus) {
    if(trend is TrendTag) {
        if(status == TrendApprovalStatus.APPROVED) {
            val res = TrendService().approveTag(trend.id.toString()).execute()
            if(res.code() != 200) {
                getStaticLoggerForTrend(trend::class.java).error("Tag ${trend.id} Trend Approval Response: ${res.code()}")
            }
        } else {
            val res = TrendService().rejectTag(trend.id.toString()).execute()
            if(res.code() != 200) {
                getStaticLoggerForTrend(trend::class.java).error("Tag ${trend.id} Trend Rejection Response: ${res.code()}")
            }
        }
    } else if(trend is TrendLink) {
        if(status == TrendApprovalStatus.APPROVED) {
            val res = TrendService().approveLink(trend.id.toString()).execute()
            if(res.code() != 200) {
                getStaticLoggerForTrend(trend::class.java).error("Link ${trend.id} Trend Approval Response: ${res.code()}")
            }
        } else {
            val res = TrendService().rejectLink(trend.id.toString()).execute()
            if(res.code() != 200) {
                getStaticLoggerForTrend(trend::class.java).error("Link ${trend.id} Trend Rejection Response: ${res.code()}")
            }
        }
    }
}