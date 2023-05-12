package de.will_smith_007.multibungeebans.redis.interfaces;

import net.md_5.bungee.api.plugin.Plugin;

public interface IRedisConnector {

    void connect();

    void closeConnection();
}
