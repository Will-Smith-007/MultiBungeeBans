package de.will_smith_007.multibungeebans.dependency_injection;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import de.will_smith_007.multibungeebans.file_config.DatabaseConfig;
import de.will_smith_007.multibungeebans.file_config.interfaces.IRedisDatabaseConfig;
import de.will_smith_007.multibungeebans.file_config.interfaces.ISQLDatabaseConfig;
import de.will_smith_007.multibungeebans.redis.RedisConnector;
import de.will_smith_007.multibungeebans.redis.RedisIncomingMessageHandler;
import de.will_smith_007.multibungeebans.redis.RedisSubscribePublishHandler;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisConnector;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisIncomingMessageHandler;
import de.will_smith_007.multibungeebans.redis.interfaces.IRedisSubscribePublishHandler;
import de.will_smith_007.multibungeebans.sql.DatabaseProvider;
import de.will_smith_007.multibungeebans.sql.HikariConfigurationHandler;
import de.will_smith_007.multibungeebans.sql.interfaces.IDatabaseProvider;
import de.will_smith_007.multibungeebans.sql.interfaces.IHikariConfigurationHandler;
import lombok.NonNull;
import net.md_5.bungee.api.plugin.Plugin;

public class InjectionModule extends AbstractModule {

    private final Plugin plugin;

    public InjectionModule(@NonNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(Plugin.class).toInstance(this.plugin);

        // Redis bindings
        bind(IRedisConnector.class).to(RedisConnector.class);
        bind(IRedisSubscribePublishHandler.class).to(RedisSubscribePublishHandler.class);
        bind(IRedisIncomingMessageHandler.class).to(RedisIncomingMessageHandler.class);
        bind(IRedisDatabaseConfig.class).to(DatabaseConfig.class);

        // SQL bindings
        bind(ISQLDatabaseConfig.class).to(DatabaseConfig.class);
        bind(IDatabaseProvider.class).to(DatabaseProvider.class);
        bind(IHikariConfigurationHandler.class).to(HikariConfigurationHandler.class);

        // Command bindings
        bind(String.class).annotatedWith(Names.named("BanCommand")).toInstance("gban");
        bind(String.class).annotatedWith(Names.named("UnbanCommand")).toInstance("unban");
        bind(String.class).annotatedWith(Names.named("TempBanCommand")).toInstance("tempban");
        bind(String.class).annotatedWith(Names.named("BanCheckCommand")).toInstance("bancheck");
        bind(String.class).annotatedWith(Names.named("BanListCommand")).toInstance("banlist");
    }
}
