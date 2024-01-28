package social.bitforged.tools.pojo

data class PluginConfig(
    /**
     * The base URL of the BitForged instance to connect to
     */
    val apiBaseUrl: String,
    /**
     * The API token to use for authentication, this can be found in the admin panel of your instance under "Development"
     * Note: This MUST have the admin read/write scopes. Read for reading the status of tags, and write for approving/rejecting tags
     */
    val apiToken: String,
    /**
     * The name of the instance, this is used in the embeds sent to Discord
     */
    val instanceName: String,
    /**
     * The ID of the channel to send trend notifications to
     */
    val trendNotificationChannelId: String,
    /**
     * The cron string to use for scheduling the trend check
     * Checkout https://crontab.guru for help with this
     */
    val cronString: String
)
