package tech.tyman.chatbridge

class ConfigManager(private val plugin: ChatBridge) {
    fun initializeConfig() {
        plugin.config.addDefault("Token", "(replace with token)")
        plugin.config.addDefault("Channel", "(replace with channel)")
        plugin.config.addDefault("Prefix", ";")
        plugin.config.options().copyDefaults(true)
        plugin.saveConfig()
    }
    var token: String
        get() = plugin.config.getString("Token")!!
        set(value) = plugin.config.set("Token", value)
    var channel: String
        get() = plugin.config.getString("Channel")!!
        set(value) = plugin.config.set("Channel", value)
    var prefix: String
        get() = plugin.config.getString("Prefix")!!
        set(value) = plugin.config.set("Prefix", value)
}