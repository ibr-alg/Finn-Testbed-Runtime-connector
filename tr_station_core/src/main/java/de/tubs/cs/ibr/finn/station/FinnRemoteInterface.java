package de.tubs.cs.ibr.finn.station;

import com.google.protobuf.InvalidProtocolBufferException;
import eu.funinnumbers.station.rmi.StationInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 22.09.11
 * Time: 13:46
 * To change this template use File | Settings | File Templates.
 */
public interface FinnRemoteInterface extends StationInterface,Remote{

    public void handle(byte[] command)
            throws RemoteException, InvalidProtocolBufferException;

}
