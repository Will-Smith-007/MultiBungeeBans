package de.will_smith_007.multibungeebans;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.will_smith_007.multibungeebans.commands.BanCommand;
import de.will_smith_007.multibungeebans.commands.TempBanCommand;
import de.will_smith_007.multibungeebans.commands.UnbanCommand;
import de.will_smith_007.multibungeebans.dependency_injection.InjectionModule;
import de.will_smith_007.multibungeebans.listeners.PlayerConnectionListener;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisConnector;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisSubscribePublishHandler;
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

        registerCommands(
                injector.getInstance(BanCommand.class),
                injector.getInstance(UnbanCommand.class),
                injector.getInstance(TempBanCommand.class)
        );

        registerListeners(
                injector.getInstance(PlayerConnectionListener.class)
        );

        getLogger().info("Have fun with banning players across multiple proxies.");
    }

    @Override
    public void onDisable() {
        injector.getInstance(IRedisSubscribePublishHandler.class).unsubscribe("BanChannel");
        injector.getInstance(IRedisConnector.class).closeConnection();

        getLogger().info("Bye!");
    }

    private void registerListeners(Listener @NonNull ... listeners) {
        for (Listener listener : listeners) {
            pluginManager.registerListener(this, listener);
        }
    }

    private void registerCommands(Command @NonNull ... commands) {
        for (Command command : commands) {
            pluginManager.registerCommand(this, command);
        }
    }
}
