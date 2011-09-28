package de.tubs.cs.ibr.finn.engine;

import de.tubs.cs.ibr.finn.ExampleProtocol;
import de.tubs.cs.ibr.finn.station.FinnTrProtocol;
import eu.funinnumbers.util.Observable;
import eu.funinnumbers.util.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PApplet;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 21.09.11
 * Time: 16:29
 * To change this template use File | Settings | File Templates.
 */
public class ExampleGame extends PApplet implements Observer {
    private static final Logger logger = LoggerFactory.getLogger(ExampleGame.class);

    public ExampleGame() {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            if (EngineManager.getInstance().getEngine() != null) {
                                FinnTrProtocol.Envelope.Builder envelope = FinnTrProtocol.Envelope.newBuilder();
                                ExampleProtocol.LedColorStatus command =
                                        ExampleProtocol.LedColorStatus.newBuilder().setBlue(100).setGreen(100)
                                                .setRed(100).setNodeAddress("urn:wisebed:tubs:408").build();
                                envelope.setId(408).setCommand(command.toByteString()).setType("LED");
                                EngineManager.getInstance().getEngine().sendCommand(envelope.build());
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Thread.interrupted();
                            }
                        }
                    }
                }).start();
    }

    @Override
    public void update(Observable observable, Object o) {
        logger.info("got exciting update, let's do some fancy visualization. In the meantime I will lite an LED");
        FinnTrProtocol.Envelope.Builder envelope = FinnTrProtocol.Envelope.newBuilder();
        ExampleProtocol.LedColorStatus command =
                ExampleProtocol.LedColorStatus.newBuilder().setBlue(0).setGreen(0).setRed(100).setNodeAddress("urn:wisebed:tubs:408")
                        .build();
        envelope.setId(408).setCommand(command.toByteString()).setType("LED");
        EngineManager.getInstance().getEngine().sendCommand(envelope.build());
    }


}
