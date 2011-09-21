package de.tubs.cs.ibr.finn.station;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 21.09.11
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public interface FinnTRStation {
    public void handle(FinnTrProtocol.Envelope command);
}
