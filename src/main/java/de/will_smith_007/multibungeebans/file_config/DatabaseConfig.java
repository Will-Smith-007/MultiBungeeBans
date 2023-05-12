package de.will_smith_007.multibungeebans.file_config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.will_smith_007.multibungeebans.file_config.interfaces.IRedisDatabaseConfig;
import de.will_smith_007.multibungeebans.file_config.interfaces.ISQLDatabaseConfig;
import lombok.NonNull;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

@Singleton
public class DatabaseConfig implements IRedisDatabaseConfig, ISQLDatabaseConfig {

    private Configuration configuration;

    @Inject
    public DatabaseConfig(@NonNull Plugin plugin) {
        final Logger logger = plugin.getLogger();

        final File databaseConfigDirectory = plugin.getDataFolder();
        final String configName = "config.yml";

        final File databaseConfig = new File(databaseConfigDirectory, configName);

        if (databaseConfigDirectory.mkdirs()) {
            logger.info("Database configuration directory was created.");
        }

        if (!databaseConfig.exists()) {
            try (InputStream inputStream = plugin.getResourceAsStream(configName)) {
                Files.copy(inputStream, databaseConfig.toPath());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(databaseConfig);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public String getRedisHost() {
        return configuration.getString("redisHost");
    }

    @Override
    public int getRedisPort() {
        return configuration.getInt("redisPort");
    }

    @Override
    public String getRedisSecret() {
        return configuration.getString("redisSecret");
    }

    @Override
    public String getSQLHost() {
        return configuration.getString("sqlHost");
    }

    @Override
    public int getSQLPort() {
        return configuration.getInt("sqlPort");
    }

    @Override
    public String getSQLDatabaseName() {
        return configuration.getString("sqlDatabase");
    }

    @Override
    public String getSQLUsername() {
        return configuration.getString("sqlUsername");
    }

    @Override
    public String getSQLSecret() {
        return configuration.getString("sqlSecret");
    }
}
