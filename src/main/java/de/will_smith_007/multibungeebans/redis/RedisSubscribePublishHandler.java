package de.will_smith_007.multibungeebans.redis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisIncomingMessageHandler;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisSubscribePublishHandler;
import lombok.NonNull;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * This class has only the responsibility to subscribe/unsubscribe to a redis channel and can be used
 * to sent data to a specific redis channel with the given data.
 */
@Singleton
public class RedisSubscribePublishHandler implements IRedisSubscribePublishHandler {

    private final RedisConnector redisConnector;
    private final IRedisIncomingMessageHandler redisSubscriptionHandler;
    private final Logger logger;

    @Inject
    public RedisSubscribePublishHandler(@NonNull RedisConnector redisConnector,
                                        @NonNull IRedisIncomingMessageHandler redisSubscriptionHandler,
                                        @NonNull Plugin plugin) {
        this.redisConnector = redisConnector;
        this.redisSubscriptionHandler = redisSubscriptionHandler;
        this.logger = plugin.getLogger();
    }

    @Override
    public void subscribe(@NonNull String channelName) {
        final JedisPool jedisPool = redisConnector.getJedisPool();

        // Thread blocking operation, must run async
        CompletableFuture.runAsync(() -> {
            final JedisPubSub jedisPubSub = redisSubscriptionHandler.handleSubscriptions(channelName);

            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(jedisPubSub, channelName);
            }
        });
    }

    @Override
    public void unsubscribe(@NonNull String channelName) {
        final JedisPubSub jedisPubSub = redisSubscriptionHandler.handleSubscriptions(channelName);
        jedisPubSub.unsubscribe(channelName);

        logger.info("Redis unsubscribed channel " + channelName);
    }

    @Override
    public void publish(@NonNull String channelName, @NonNull String data) {
        final JedisPool jedisPool = redisConnector.getJedisPool();

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(channelName, data);
        }
    }
}
