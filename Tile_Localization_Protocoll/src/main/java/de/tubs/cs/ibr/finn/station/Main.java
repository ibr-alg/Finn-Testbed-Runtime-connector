package de.tubs.cs.ibr.finn.station;

import com.google.common.collect.Lists;
import de.tubs.cs.ibr.finn.handlers.TileLoadedHandler;
import de.uniluebeck.itm.netty.handlerstack.isense.ISensePacketDecoder;
import de.uniluebeck.itm.netty.handlerstack.isense.ISensePacketEncoder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.helpers.Loader;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 13.09.11
 * Time: 10:35
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    static final Options options = new Options();


    static {
        options.addOption("H", "Host", true, "Protobuf Host");
        options.addOption("p", "port", true, "Protobuf port");
        options.addOption("r", "reservation", true, "Secret reservation key");
        options.addOption("f", "file", true, "Handler stack configuration file");
        options.addOption("v", "verbose", false, "Verbose logging output");
        options.addOption("x", "xtremlyverbose", false, "Extremly verbose logging output");
        options.addOption("h", "help", false, "Help output");


    }

    private static void configureLoggingDefaults() {
        PatternLayout patternLayout = new PatternLayout("%-13d{HH:mm:ss,SSS} | %-25.25c{2} | %-5p | %m%n");

        final Appender appender = new ConsoleAppender(patternLayout);
        org.apache.log4j.Logger.getRootLogger().removeAllAppenders();
        org.apache.log4j.Logger.getRootLogger().addAppender(appender);
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);
    }

    public static void main(String[] args) {
//        String secretReservationKeys = System.getProperty("testbed.secretreservationkeys");
//        String pccHost = System.getProperty("testbed.protobuf.hostname");
//        Integer pccPort = Integer.parseInt(System.getProperty("testbed.protobuf.port"));

//--------------------------------------------------------------------------
// Application logic
//--------------------------------------------------------------------------

        configureLoggingDefaults();
        final org.slf4j.Logger log = LoggerFactory.getLogger(Main.class);

        // Create a handler factory and populate it with all MOVEDETECT factories
//        HandlerFactoryRegistry factoryRegistry = new HandlerFactoryRegistry();
//        ProtocolCollection.registerProtocols(factoryRegistry);
//        try {
//            factoryRegistry.register(new TileLoadedHandlerFactory());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        // Options set from the command line
        String protobufHost = null;
        int protobufPort = 8885;
        String secretReservationKeys = null;
        File xmlConfigFile = null;

        // Parse the command line
        try {
            CommandLine line = new PosixParser().parse(options, args);

            org.apache.log4j.Logger.getRootLogger().setLevel(Level.INFO);

            // Check if verbose output should be used
            if (line.hasOption('v')) {
                org.apache.log4j.Logger.getRootLogger().setLevel(Level.DEBUG);
            }

            // Check if xtremlyverbose output should be used
            if (line.hasOption('x')) {
                org.apache.log4j.Logger.getRootLogger().setLevel(Level.TRACE);
            }

            // Output help and exit
            if (line.hasOption('h')) {
                usage(options);
            }

            if (line.hasOption('H')) {
                protobufHost = line.getOptionValue('H');
            } else {
                throw new Exception("Please supply -H");
            }

            if (line.hasOption('p')) {
                protobufPort = Integer.parseInt(line.getOptionValue('p'));
            } else {
                throw new Exception("Please supply -p");
            }

            if (line.hasOption('r')) {
                secretReservationKeys = line.getOptionValue('r');
            } else {
                throw new Exception("Please supply -r");
            }

            if (line.hasOption('f')) {
                xmlConfigFile = new File(line.getOptionValue('f'));
            } else {
                try {
                    xmlConfigFile = new File(Loader.getResource("finn-handler-stack.xml").toURI());
                } catch (Exception e) {
                    throw new Exception("loading default handlers failed, Please supply -f");
                }
            }

        } catch (Exception e) {
            log.error("Invalid command line: " + e, e);
            usage(options);
            System.exit(1);
        }

        GenericFinnStation station = new GenericFinnStation(
                new TestbedConnector(protobufHost, protobufPort, secretReservationKeys, xmlConfigFile));
        station.getConnector().getBootstrap().setPipelineFactory(
                new ChannelPipelineFactory() {
                    public ChannelPipeline getPipeline()
                            throws Exception {
                        final ChannelPipeline pipeline = pipeline();
//                pipeline.addLast("dle-etx-decoder", new DleStxEtxFramingDecoder(null));
//                pipeline.addLast("dle-etx-encoder", new DleStxEtxFramingEncoder(null));
                        pipeline.addLast("isense-decoder", new ISensePacketDecoder());
                        pipeline.addLast("isense-encoder", new ISensePacketEncoder());
                        pipeline.addLast("Tile-Events-Handler", new TileLoadedHandler(null));
                        return pipeline;
                    }
                });
        ConnectorManager.getInstance().setConnector(station.getConnector());
        station.getConnector().connect();
        try {
            station.register_Station(new ExampleStation());
        } catch (RemoteException e) {
            log.error("error registering station", e);
        }



    }

    @SuppressWarnings("unused")
    public static List<eu.wisebed.api.sm.SecretReservationKey> parseSecretReservationKeys(String str) {
        String[] pairs = str.split(";");
        List<eu.wisebed.api.sm.SecretReservationKey> keys = Lists.newArrayList();
        for (String pair : pairs) {
            String urnPrefix = pair.split(",")[0];
            String secretReservationKeys = pair.split(",")[1];
            eu.wisebed.api.sm.SecretReservationKey key = new eu.wisebed.api.sm.SecretReservationKey();
            key.setUrnPrefix(urnPrefix);
            key.setSecretReservationKey(secretReservationKeys);
            keys.add(key);
        }
        return keys;
    }

    private static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(120, Main.class.getCanonicalName(), null, options, null);
        System.exit(1);
    }
}
