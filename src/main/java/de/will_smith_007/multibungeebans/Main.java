package de.will_smith_007.multibungeebans;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.will_smith_007.multibungeebans.commands.*;
import de.will_smith_007.multibungeebans.dependency_injection.InjectionModule;
import de.will_smith_007.multibungeebans.listeners.PlayerConnectionListener;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisConnector;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisSubscribePublishHandler;
import de.will_smith_007.multibungeebans.sql.interfaces.IDatabaseConnector;
import lombok.NonNull;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class Main extends Plugin {

    private final Injector injector = Guice.createInjector(new InjectionModule(this));
    private final PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();

    @Override
    public void onEnable() {
        injector.getInstance(IRedisConnector.class).connect();
        injector.getInstance(IRedisSubscribePublishHandler.class).subscribe("BanChannel");

        // Command registration
        registerCommands(
                injector.getInstance(BanCommand.class),
                injector.getInstance(UnbanCommand.class),
                injector.getInstance(TempBanCommand.class),
                injector.getInstance(BanCheckCommand.class),
                injector.getInstance(BanListCommand.class)
        );

        // Listener registration
        registerListeners(
                injector.getInstance(PlayerConnectionListener.class)
        );

        getLogger().info("Have fun with banning players across multiple proxies.");
    }

    @Override
    public void onDisable() {
        injector.getInstance(IRedisSubscribePublishHandler.class).unsubscribe("BanChannel");
        injector.getInstance(IRedisConnector.class).closeConnection();
        injector.getInstance(IDatabaseConnector.class).closeConnection();

        getLogger().info("Bye!");
    }

    /**
     * Registers all listeners in the specified {@link Listener} array.
     *
     * @param listeners Classes which implements the {@link Listener} interface.
     */
    private void registerListeners(Listener @NonNull ... listeners) {
        for (Listener listener : listeners) {
            pluginManager.registerListener(this, listener);
        }
    }

    /**
     * Registers all commands in the specified {@link Command} array.
     *
     * @param commands Classes which are extending from the {@link Command} abstract class.
     */
    private void registerCommands(Command @NonNull ... commands) {
        for (Command command : commands) {
            pluginManager.registerCommand(this, command);
        }
    }
}
