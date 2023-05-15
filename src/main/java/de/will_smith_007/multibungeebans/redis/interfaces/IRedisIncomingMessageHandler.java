package de.will_smith_007.multibungeebans.redis.interfaces;

import lombok.NonNull;
import redis.clients.jedis.JedisPubSub;

public interface IRedisIncomingMessageHandler {

    /**
     * Handles all redis subscriptions the plugin has made.
     *
     * @param channelName Name of Channel on which you want to handle received messages.
     * @return A {@link JedisPubSub} which can be used to subscribe/unsubscribe to channels.
     */
    @NonNull JedisPubSub handleSubscriptions(@NonNull String channelName);
}
