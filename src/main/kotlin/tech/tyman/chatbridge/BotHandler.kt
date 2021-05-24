package tech.tyman.chatbridge

import com.github.shynixn.mccoroutine.registerSuspendingEvents
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.exception.KordInitializationException
import dev.kord.core.on
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import java.util.logging.Level

class BotHandler(val plugin: ChatBridge) {
    lateinit var client: Kord
    suspend fun startBot() {
        try {
            client = Kord(plugin.configManager.Token)
        } catch (e: KordInitializationException) {
            plugin.logger.log(Level.SEVERE, "Unable to log into discord, is the token set and valid?")
            return
        }
        client.on<MessageCreateEvent> {
            if (this.guildId == null) return@on
            plugin.server.broadcast(
                Component.text(
                    "[Discord] ${this.member!!.displayName}: ${this.message.content}"
                ),
                "chatbridge.messages.viewsent"
            )
        }
        plugin.server.pluginManager.registerSuspendingEvents(ServerEventHandler(), plugin)
    }
    @Suppress("unused")
    inner class ServerEventHandler : Listener {
        @EventHandler
        suspend fun onPlayerChat(event: AsyncChatEvent) {
            if (!event.isAsynchronous) {
                // A non-async message means the player did not actually send it,
                // a plugin did. I might make this work later but not right now
                plugin.logger.log(Level.INFO, "Got sync message, ignoring")
                return
            }
            val channel = client.getChannel(Snowflake(plugin.configManager.Channel))
            if (channel == null) {
                plugin.logger.log(
                    Level.SEVERE,
                    "Could not fetch channel from config and therefore could not send message to it!"
                )
                return
            }
            if (channel.type != ChannelType.GuildText) {
                plugin.logger.log(
                    Level.SEVERE,
                    "Channel must be a Text channel!"
                )
                return
            }
            (channel as TextChannel).createMessage(event.message().toString())
        }
    }
}