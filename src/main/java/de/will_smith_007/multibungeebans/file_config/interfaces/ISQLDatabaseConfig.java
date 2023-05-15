package de.will_smith_007.multibungeebans.file_config.interfaces;

/**
 * This interface has only the responsibility to get the connection information from the sql
 * configured values.
 */
public interface ISQLDatabaseConfig {

    String getSQLHost();

    int getSQLPort();

    String getSQLDatabaseName();

    String getSQLUsername();

    String getSQLSecret();
}
