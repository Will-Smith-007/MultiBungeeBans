package de.will_smith_007.multibungeebans.redis;

import com.google.inject.Singleton;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisIncomingMessageHandler;
import lombok.NonNull;
import redis.clients.jedis.JedisPubSub;

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
                //TODO: Handle the ban subscription
            }
        };
    }
}
