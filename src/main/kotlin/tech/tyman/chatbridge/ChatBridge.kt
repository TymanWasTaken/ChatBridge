package tech.tyman.chatbridge

import co.aikar.commands.PaperCommandManager
import com.github.shynixn.mccoroutine.SuspendingJavaPlugin
import java.util.logging.Level

class ChatBridge : SuspendingJavaPlugin() {
    lateinit var configManager: ConfigManager
    private lateinit var botHandler: BotHandler
    private lateinit var commandManager: PaperCommandManager
    override suspend fun onEnableAsync() {
        commandManager = PaperCommandManager(this)
        configManager = ConfigManager(this)
        configManager.initializeConfig()
        botHandler = BotHandler(this)
        botHandler.startBot()
        this.logger.log(Level.INFO, "ChatBridge Loaded")
    }
    override suspend fun onDisableAsync() {
        botHandler.client?.shutdown()
        configManager.save()
        this.logger.log(Level.INFO, "ChatBridge Unloaded")
    }
}