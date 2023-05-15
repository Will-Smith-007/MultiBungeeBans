package de.will_smith_007.multibungeebans.redis;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.multibungeebans.file_config.interfaces.IRedisDatabaseConfig;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisConnector;
import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.plugin.Plugin;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.logging.Logger;

/**
 * This class has only the responsibility to handle redis connections.
 */
@Singleton
public class RedisConnector implements IRedisConnector {

    @Getter
    private JedisPool jedisPool;
    private final Logger logger;
    private final IRedisDatabaseConfig redisDatabaseConfig;

    @Inject
    public RedisConnector(@NonNull Plugin plugin,
                          @NonNull IRedisDatabaseConfig redisDatabaseConfig) {
        this.logger = plugin.getLogger();
        this.redisDatabaseConfig = redisDatabaseConfig;
    }

    @Override
    public void connect() {
        if (jedisPool != null) return;

        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(128);
        jedisPoolConfig.setMaxIdle(128);
        jedisPoolConfig.setMinIdle(16);

        jedisPool = new JedisPool(jedisPoolConfig,
                redisDatabaseConfig.getRedisHost(),
                redisDatabaseConfig.getRedisPort(),
                5000,
                redisDatabaseConfig.getRedisSecret());

        logger.info("Redis connection established.");
    }

    @Override
    public void closeConnection() {
        jedisPool.close();

        logger.info("Redis connection was closed.");
    }
}
