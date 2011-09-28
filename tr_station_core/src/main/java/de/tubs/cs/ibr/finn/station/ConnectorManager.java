package de.tubs.cs.ibr.finn.station;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 22.09.11
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
public class ConnectorManager {

    private static ConnectorManager instance;

     private TestbedConnector connector = null;


    private ConnectorManager() {
    }

    /**
     * Provides access to the single instance of the manager.
     *
     * @return reference to the unique instance of the EngineManager.
     */
    public static ConnectorManager getInstance() {
        synchronized (ConnectorManager.class) {
            if (instance == null) {
                instance = new ConnectorManager();
            }
        }

        return instance;
    }

    public TestbedConnector getConnector() {
        return connector;
    }

    public void setConnector(TestbedConnector connector) {
        this.connector = connector;
    }
}
