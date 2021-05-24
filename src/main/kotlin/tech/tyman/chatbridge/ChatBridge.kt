package tech.tyman.chatbridge

import kotlinx.coroutines.runBlocking
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level
import kotlin.concurrent.thread

class ChatBridge : JavaPlugin() {
    lateinit var configManager: ConfigManager
    private lateinit var botHandler: BotHandler
    override fun onEnable() {
        configManager = ConfigManager(this)
        botHandler = BotHandler(this)
        thread {
            runBlocking {
                botHandler.startBot()
            }
        }
        this.logger.log(Level.INFO, "ChatBridge Loaded")
    }
    override fun onDisable() {
        saveConfig()
        this.logger.log(Level.INFO, "ChatBridge Unloaded")
    }
}