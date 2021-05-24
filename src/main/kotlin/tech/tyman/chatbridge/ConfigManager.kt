package tech.tyman.chatbridge

class ConfigManager(val plugin: ChatBridge) {
    fun initializeConfig() {
        plugin.config.addDefault("Token", "(replace with token)")
        plugin.config.addDefault("Channel", "(replace with channel)")
        plugin.config.addDefault("Prefix", ";")
        plugin.config.options().copyDefaults(true)
        plugin.saveConfig()
    }
    var Token: String
        get() = plugin.config.getString("Token")!!
        set(value) = plugin.config.set("Token", value)
    var Channel: String
        get() = plugin.config.getString("Channel")!!
        set(value) = plugin.config.set("Channel", value)
    var Prefix: String
        get() = plugin.config.getString("Prefix")!!
        set(value) = plugin.config.set("Prefix", value)
}