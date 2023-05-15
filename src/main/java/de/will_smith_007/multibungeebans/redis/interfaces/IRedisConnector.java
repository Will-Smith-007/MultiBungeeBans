package de.will_smith_007.multibungeebans.redis.interfaces;

public interface IRedisConnector {

    /**
     * Tries to establish a connection to the redis database.
     */
    void connect();

    /**
     * Tries to close an established connection to the redis database.
     */
    void closeConnection();
}
