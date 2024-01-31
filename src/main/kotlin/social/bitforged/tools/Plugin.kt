package social.bitforged.tools

import net.system86.jist.lib.plugin.impl.DiscordPlugin
import org.javacord.api.DiscordApi
import social.bitforged.tools.commands.ListTrendingTagsCommand
import social.bitforged.tools.etc.ConfigManager
import social.bitforged.tools.etc.ScheduleManager

class Plugin: DiscordPlugin() {

    init {
        logger.info("Initializing Mastodon Trends Notifier...")
        logger.info("Found Kotlin Standard Library version at runtime: ${KotlinVersion.CURRENT}")
    }

    override fun onEnable(discordApi: DiscordApi?) {
        logger.info("Done! You can invite me to your server with this link: ${discordApi?.createBotInvite()}")
        logger.info("--------------------------")
        pluginManager.registerCommand(this, "list-trending-tags", ListTrendingTagsCommand::class.java)

        // Initialize managers
        ConfigManager(configFolder.path)
        ScheduleManager(this)
    }

    override fun onDisable() {
        logger.info("Bye for now!")
    }
}