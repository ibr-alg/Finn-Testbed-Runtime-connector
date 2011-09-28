package de.tubs.cs.ibr.finn.engine;

import com.google.protobuf.InvalidProtocolBufferException;
import de.tubs.cs.ibr.finn.station.FinnRemoteInterface;
import de.tubs.cs.ibr.finn.station.FinnTrProtocol;
import eu.funinnumbers.engine.AbstractEngineApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 21.09.11
 * Time: 10:26
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTestbedEngineApp extends AbstractEngineApp {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTestbedEngineApp.class);

    public void sendCommand(FinnTrProtocol.Envelope command) {
        try {
            for (FinnRemoteInterface finnRemoteInterface : EngineManager.getInstance().getTRStations()) {

                finnRemoteInterface.handle(command.toByteArray());

            }
        } catch (RemoteException e) {
            logger.error("unable to send command", e);
        } catch (InvalidProtocolBufferException e) {
            logger.error("unable to send command", e);
        }
    }

}
