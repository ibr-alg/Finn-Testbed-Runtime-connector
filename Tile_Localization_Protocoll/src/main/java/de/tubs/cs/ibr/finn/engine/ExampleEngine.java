package de.tubs.cs.ibr.finn.engine;

import eu.funinnumbers.engine.rmi.SGEngineRMIImpl;
import eu.funinnumbers.engine.util.GenericRMIServer;
import eu.funinnumbers.util.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 21.09.11
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
public class ExampleEngine extends AbstractTestbedEngineApp{
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(ExampleEngine.class);

           static final Options options = new Options();


    static {
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

    /**
     * Game Specific code.
     */
    public void startApp() {

        Logger.getInstance().debug("HAL Engine Started");
        //Initialize Engine RMI implementation
        try {
            final SGEngineRMIImpl engineImpl = new SGEngineRMIImpl();
            engineImpl.setEngineApp(this);

            //Register RMI Interface
            GenericRMIServer.getInstance().registerInterface(engineImpl.RMI_NAME, engineImpl);

        } catch (RemoteException e) {
            Logger.getInstance().debug("Unable to register RMI interface", e);
        }

        EngineManager.getInstance().setEngine(this);

        new ExampleFrame();


    }

    /**
     * ExampleEngine function.
     *
     * @param args String Argument
     */
    public static void main(final String[] args) {
        // Initialize HAL eu.funinnumbers.engine
        // Parse the command line
        configureLoggingDefaults();
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

        } catch (Exception e) {
            log.error("Invalid command line: " + e, e);
            usage(options);
            System.exit(1);
        }
        ExampleEngine exampleEngine = new ExampleEngine();


    }

       private static void usage(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(120, ExampleEngine.class.getCanonicalName(), null, options, null);
        System.exit(1);
    }

}
