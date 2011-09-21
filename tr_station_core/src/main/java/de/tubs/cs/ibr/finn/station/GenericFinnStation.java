package de.tubs.cs.ibr.finn.station;

import eu.funinnumbers.engine.event.EventForwarder;
import eu.funinnumbers.engine.rmi.SGEngineInterface;
import eu.funinnumbers.station.AbstractStationApp;
import eu.funinnumbers.station.rmi.StationInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.Naming;
import java.rmi.RemoteException;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 13.09.11
 * Time: 18:45
 * To change this template use File | Settings | File Templates.
 */
public class GenericFinnStation extends AbstractStationApp implements FinnTRStation{
    private static final Logger logger = LoggerFactory.getLogger(GenericFinnStation.class);

    protected SGEngineInterface engine;
    private TestbedConnector connector;

    @Override
    public void stationApp() {
        //To change body of implemented methods use File | Settings | File Templates.
        try {
            //Lookup Engines Interface
            engine = (SGEngineInterface) Naming.lookup("rmi://" + getEngineIP() + "/" + SGEngineInterface.RMI_NAME);
            // Send event to Coordinator
            EventForwarder.getInstance().setEngineInterface(engine);


        } catch (Exception e) {
            logger.error("error initializing Generic Station App",e);

        }
    }

    public SGEngineInterface getEngine() {
        return engine;
    }

    public void setEngine(SGEngineInterface engine) {
        this.engine = engine;
    }

    public void register_Station(StationInterface station)
            throws RemoteException {
        //Register Station to Engine
        engine.registerStation(getMyIP(), station);
    }

    @Override
    public void handle(FinnTrProtocol.Envelope command) {
        connector.handle(command);
    }

    public GenericFinnStation(TestbedConnector connector) {
        this.connector = connector;
    }
}
