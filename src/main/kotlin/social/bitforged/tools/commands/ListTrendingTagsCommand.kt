package social.bitforged.tools.commands

import net.system86.jist.lib.command.iface.Command
import net.system86.jist.lib.command.slash.DiscordCommand
import net.system86.jist.lib.interactions.impl.pagination.EmbedFieldContainer
import net.system86.jist.lib.interactions.impl.pagination.PaginationInteraction
import org.javacord.api.entity.message.embed.EmbedBuilder
import social.bitforged.tools.etc.ConfigManager
import social.bitforged.tools.services.TrendService
import java.awt.Color

class ListTrendingTagsCommand: DiscordCommand() {
    @Command(description = "List trending tags from Mastodon", allowInDms = false)
    fun execute() {
        val tags = TrendService().getTrendingTags()
        if(tags == null) {
            replyNow("Failed to get trending tags!")
            return
        }
        val instanceName = ConfigManager.getInstance().getConfig().instanceName
        val embedBuilder = EmbedBuilder()
        embedBuilder.setTitle("Trending Tags for $instanceName")
        embedBuilder.setDescription("These are the trending tags for $instanceName. The ones marked 'true' require review.")
        embedBuilder.setColor(Color.getHSBColor(259.6f, 48.9f, 85.9f))
        val fieldContainers = ArrayList<EmbedFieldContainer>()
        tags.forEachIndexed { _, tag ->
            fieldContainers.add(EmbedFieldContainer(tag.name, tag.requiresReview.toString(), false))
        }

        val paginatedInteraction = PaginationInteraction(interaction, plugin, embedBuilder, *fieldContainers.toTypedArray())
        paginatedInteraction.updateOrSpawnEmbed()
        logger.info("Found ${tags.size} trending tags!")
    }
}