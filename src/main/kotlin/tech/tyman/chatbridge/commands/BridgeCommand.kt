package tech.tyman.chatbridge.commands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.github.shynixn.mccoroutine.launchAsync
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.TextChannel
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import tech.tyman.chatbridge.BotHandler
import tech.tyman.chatbridge.ChatBridge
import tech.tyman.chatbridge.ConfigManager

@CommandAlias("chatbridge")
class BridgeCommand : BaseCommand() {
    @Dependency
    lateinit var plugin: ChatBridge

    @Subcommand("say")
    @Syntax("<message>")
    @Description("Sends a message as the bot user in the configured channel")
    @Conditions("botLoaded")
    fun sayCommand(player: Player, channel: TextChannel, args: Array<String>) = plugin.launchAsync {
        channel.createMessage(args.joinToString(" "))
    }
}