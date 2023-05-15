package de.will_smith_007.multibungeebans.file_config.interfaces;

/**
 * This interface has only the responsibility to get the connection information from the redis
 * configured values.
 */
public interface IRedisDatabaseConfig {

    String getRedisHost();

    int getRedisPort();

    String getRedisSecret();
}
