package tech.tyman.chatbridge

import co.aikar.commands.ConditionFailedException
import co.aikar.commands.PaperCommandManager
import com.github.shynixn.mccoroutine.SuspendingJavaPlugin
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.TextChannel
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import tech.tyman.chatbridge.commands.*
import java.util.logging.Level

class ChatBridge : SuspendingJavaPlugin() {
    lateinit var configManager: ConfigManager
    private lateinit var botHandler: BotHandler
    private lateinit var commandManager: PaperCommandManager
    override suspend fun onEnableAsync() {
        configManager = ConfigManager(this)
        configManager.initializeConfig()
        botHandler = BotHandler(this)
        botHandler.startBot()
        commandManager = PaperCommandManager(this)
        commandManager.setup()
        this.logger.log(Level.INFO, "ChatBridge Loaded")
    }
    override suspend fun onDisableAsync() {
        botHandler.client?.shutdown()
        configManager.save()
        this.logger.log(Level.INFO, "ChatBridge Unloaded")
    }

    private fun PaperCommandManager.setup() {
        this.registerDependency(BotHandler::class.java, botHandler)
        this.registerDependency(ConfigManager::class.java, configManager)
        this.commandConditions.addCondition("botLoaded") {
            if (botHandler.client == null)
                throw ConditionFailedException("Bot is not logged in, check console to check for any error messages.")
        }
        this.registerCommand(BridgeCommand())
    }
}