package de.tubs.cs.ibr.finn.engine;

import de.tubs.cs.ibr.finn.station.FinnTRStation;
import de.tubs.cs.ibr.finn.station.FinnTrProtocol;
import eu.funinnumbers.engine.AbstractEngineApp;
import eu.funinnumbers.station.rmi.StationInterface;

import java.util.Collection;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 21.09.11
 * Time: 10:26
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTestbedEngineApp extends AbstractEngineApp{

    public Collection<FinnTRStation> getTRStations(){
        Collection<FinnTRStation> out = new Vector<FinnTRStation>();
        for (StationInterface stationInterface : stationInterfaces.values()) {
            if (stationInterface instanceof FinnTRStation) {
                FinnTRStation trStation = (FinnTRStation) stationInterface;
                out.add(trStation);
            }
        }
        return out;
    }

    public void sendCommand(FinnTrProtocol.Envelope command){
        for (FinnTRStation stationInterface : getTRStations()) {
            stationInterface.handle(command);
        }
    }

}
