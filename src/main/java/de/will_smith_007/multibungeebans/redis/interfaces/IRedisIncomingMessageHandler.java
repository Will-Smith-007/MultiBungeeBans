package de.will_smith_007.multibungeebans.redis.interfaces;

import lombok.NonNull;
import redis.clients.jedis.JedisPubSub;

public interface IRedisIncomingMessageHandler {

    @NonNull JedisPubSub handleSubscriptions(@NonNull String channelName);
}
