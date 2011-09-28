package de.tubs.cs.ibr.finn.station;

import com.google.protobuf.InvalidProtocolBufferException;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.util.eventconsumer.EventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 22.09.11
 * Time: 13:30
 * To change this template use File | Settings | File Templates.
 */
public class ExampleStation extends UnicastRemoteObject implements FinnRemoteInterface,Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ExampleStation.class);

    public ExampleStation()
            throws RemoteException {
        super();
    }

    @Override
    public void handle(byte[] command)
            throws RemoteException, InvalidProtocolBufferException {
        ConnectorManager.getInstance().getConnector().handle(FinnTrProtocol.Envelope.parseFrom(command));
    }

    @Override
    public void addEvent(Event event)
            throws RemoteException {
        EventConsumer.getInstance().addEvent(event);
    }

    @Override
    public void completeRegistration()
            throws RemoteException {
        logger.info("Registerd station with ENgine succesfully");
    }

    
}
