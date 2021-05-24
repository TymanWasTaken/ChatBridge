package tech.tyman.chatbridge

import com.github.shynixn.mccoroutine.registerSuspendingEvents
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createWebhook
import dev.kord.core.behavior.execute
import dev.kord.core.entity.Webhook
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.exception.KordInitializationException
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import io.papermc.paper.event.player.AsyncChatEvent
import kotlinx.coroutines.flow.firstOrNull
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import java.util.logging.Level

class BotHandler(val plugin: ChatBridge) {
    lateinit var client: Kord
    lateinit var webhook: Webhook
    suspend fun startBot() {
        try {
            client = Kord(plugin.configManager.token) {
                intents = Intents {
                    +Intent.GuildMessages
                    +Intent.DirectMessages
                    +Intent.Guilds
                }
                cache {
                    messages(none())
                    emojis(none())
                    presences(none())
                    voiceState(none())
                }
            }
        } catch (e: KordInitializationException) {
            plugin.logger.log(Level.SEVERE, "Unable to log into discord, is the token set and valid?")
            return
        }
        client.on<MessageCreateEvent> {
            if (this.guildId == null) return@on
            if (this.message.webhookId != null) return@on
            plugin.server.broadcast(
                Component.text(
                    "[Discord] ${this.member!!.displayName}: ${this.message.content}"
                ),
                "chatbridge.messages.viewsent"
            )
        }
        client.on<ReadyEvent> {
            var channel = client.getChannel(Snowflake(plugin.configManager.channel))
            if (channel == null) {
                plugin.logger.log(Level.SEVERE, "Could not fetch channel, stopping bot...")
                client.shutdown()
                return@on
            }
            if (channel.type != ChannelType.GuildText) {
                plugin.logger.log(Level.SEVERE, "Set channel must be a text channel, stopping bot...")
                client.shutdown()
                return@on
            }
            channel = channel as TextChannel
            val foundWebhook = channel.webhooks.firstOrNull()
            webhook = foundWebhook ?: channel.createWebhook(
                "ChatBridge chat webhook"
            )
        }
        plugin.server.pluginManager.registerSuspendingEvents(ServerEventHandler(), plugin)
        plugin.logger.log(Level.INFO, "Logged in to discord!")
        client.login {
            watching("both Discord and Minecraft")
        }
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
            val channel = client.getChannel(Snowflake(plugin.configManager.channel))
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
            webhook.execute(webhook.token!!) {
                content = (event.message() as TextComponent).content()
                avatarUrl = "https://crafatar.com/avatars/${event.player.uniqueId}?overlay"
                username = (event.player.displayName() as TextComponent).content()
            }
        }
    }
}