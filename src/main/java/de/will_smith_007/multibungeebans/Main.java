package de.will_smith_007.multibungeebans;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.will_smith_007.multibungeebans.dependency_injection.InjectionModule;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisConnector;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisSubscribePublishHandler;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

    private final Injector injector = Guice.createInjector(new InjectionModule(this));

    @Override
    public void onEnable() {
        injector.getInstance(IRedisConnector.class).connect();
        injector.getInstance(IRedisSubscribePublishHandler.class).subscribe("BanChannel");

        getLogger().info("Have fun with banning players across multiple proxies.");
    }

    @Override
    public void onDisable() {
        injector.getInstance(IRedisSubscribePublishHandler.class).unsubscribe("BanChannel");
        injector.getInstance(IRedisConnector.class).closeConnection();

        getLogger().info("Bye!");
    }
}
