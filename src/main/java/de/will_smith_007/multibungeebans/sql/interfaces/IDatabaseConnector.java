package de.will_smith_007.multibungeebans.sql.interfaces;

public interface IDatabaseConnector {

    /**
     * Tries to establish a connection to the sql database.
     */
    void connect();

    /**
     * Tries to close an established connection to the sql database.
     */
    void closeConnection();
}
