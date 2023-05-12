package de.will_smith_007.multibungeebans.redis.interfaces;

import lombok.NonNull;

public interface IRedisSubscribePublishHandler {

    void subscribe(@NonNull String channelName);

    void unsubscribe(@NonNull String channelName);

    void publish(@NonNull String channelName, @NonNull String data);
}
