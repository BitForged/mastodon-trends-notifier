package social.bitforged.tools.etc

import net.system86.jist.lib.logging.Loggable
import org.json.JSONObject
import social.bitforged.tools.pojo.PluginConfig
import java.io.File

class ConfigManager(private val configFolderPath: String): Loggable {
    private val config: PluginConfig

    companion object {
        private var instance: ConfigManager? = null

        fun getInstance(): ConfigManager {
            return instance!!
        }
    }

    init {
        logger.info("Loading config...")
        instance = this
        val configFolder = File(configFolderPath)
        if (!configFolder.exists()) {
            configFolder.mkdirs()
        }

        // Check if config file exists
        val configFile = File("$configFolderPath/config.json")
        if (!configFile.exists()) {
            // Copy config from jar resources into folder
            val configResource = javaClass.classLoader.getResourceAsStream("config.json")
            configFile.createNewFile()
            if (configResource != null) {
                logger.info("No config file found, creating one from defaults...")
                configFile.writeBytes(configResource.readBytes())
                logger.info("Done creating default config! It can be found in $configFolderPath/config.json")
                logger.warn("Please edit the config and restart the bot!")
            } else {
                throw Exception("Could not find config.json in resources!")
            }
        }
        val rawConfig = JSONObject(configFile.readText())
        config = PluginConfig(
            rawConfig.getString("apiBaseUrl"),
            rawConfig.getString("apiToken"),
            rawConfig.getString("instanceName"),
            rawConfig.getString("trendNotificationChannelId"),
            rawConfig.getString("cronString")
        )
        logger.info("Config parsed, Instance name: ${config.instanceName}")
        logger.info("Done loading config!")
    }

    fun getConfig(): PluginConfig {
        return config
    }
}