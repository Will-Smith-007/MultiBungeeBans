package de.will_smith_007.multibungeebans.redis.interfaces;

import lombok.NonNull;

public interface IRedisSubscribePublishHandler {

    /**
     * Subscribes to a redis channel with the given name.
     *
     * @param channelName The name of channel which should be subscribed.
     */
    void subscribe(@NonNull String channelName);

    /**
     * Unsubscribes from a redis channel with the given name.
     *
     * @param channelName The name of channel which should be unsubscribed.
     */
    void unsubscribe(@NonNull String channelName);

    /**
     * Sends data to a specific redis channel with the given name.
     *
     * @param channelName Name of channel to which the data should be sent.
     * @param data        Data which should be sent to the channel.
     */
    void publish(@NonNull String channelName, @NonNull String data);
}
