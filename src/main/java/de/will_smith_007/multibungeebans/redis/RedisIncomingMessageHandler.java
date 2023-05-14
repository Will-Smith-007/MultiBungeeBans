package de.will_smith_007.multibungeebans.redis;

import com.google.inject.Singleton;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisIncomingMessageHandler;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.JedisPubSub;

import java.util.UUID;

@Singleton
public class RedisIncomingMessageHandler implements IRedisIncomingMessageHandler {

    private volatile JedisPubSub jedisPubSub;

    @Override
    public @NonNull JedisPubSub handleSubscriptions(@NonNull String channelName) {
        if (jedisPubSub != null) return jedisPubSub;

        return jedisPubSub = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                if (!channel.equals(channelName)) return;

                final String[] commandArgs = message.split("#");
                if (commandArgs[0].equalsIgnoreCase("ban")) {
                    final UUID playerUUID = UUID.fromString(commandArgs[1]);
                    final ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerUUID);

                    if (proxiedPlayer == null) return;

                    final String formattedKickMessage = commandArgs[2];

                    proxiedPlayer.disconnect(new TextComponent(formattedKickMessage));
                }
            }
        };
    }
}
