package de.tubs.cs.ibr.finn.station;

import de.tubs.cs.ibr.finn.TileLoadedHandlerFactory;
import de.uniluebeck.itm.netty.handlerstack.FilterHandler;
import de.uniluebeck.itm.netty.handlerstack.FilterPipeline;
import de.uniluebeck.itm.netty.handlerstack.FilterPipelineImpl;
import de.uniluebeck.itm.netty.handlerstack.HandlerFactoryRegistry;
import de.uniluebeck.itm.netty.handlerstack.protocolcollection.ProtocolCollection;
import de.uniluebeck.itm.nettywisebed.WisebedChannelFactory;
import de.uniluebeck.itm.nettywisebed.WisebedTestbedAddress;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 21.09.11
 * Time: 11:26
 * To change this template use File | Settings | File Templates.
 */
public class TestbedConnector {
    public void handle(FinnTrProtocol.Envelope command) {
        //To change body of created methods use File | Settings | File Templates.
    }

    // Create a handler factory and populate it with all MOVEDETECT factories
    HandlerFactoryRegistry factoryRegistry;

    // Options set from the command line
    private String protobufHost = null;
    private int protobufPort = 8885;
    private String secretReservationKeys = null;
    private File xmlConfigFile = null;
    private final Executor executorService;
    private ClientBootstrap bootstrap;
    private FilterPipeline filterPipeline;
    private final FilterHandler filterHandler;
    private final Channel channel;


    public TestbedConnector(String protobufHost, int protobufPort, String secretReservationKeys, File xmlConfigFile) {
        this.protobufHost = protobufHost;
        this.protobufPort = protobufPort;
        this.secretReservationKeys = secretReservationKeys;
        this.xmlConfigFile = xmlConfigFile;

        factoryRegistry = new HandlerFactoryRegistry();
        ProtocolCollection.registerProtocols(factoryRegistry);
        try {
            factoryRegistry.register(new TileLoadedHandlerFactory());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        executorService = Executors.newCachedThreadPool();
		bootstrap = new ClientBootstrap(new WisebedChannelFactory(executorService, executorService));

        filterPipeline = new FilterPipelineImpl();
        try {
            filterPipeline.setChannelPipeline(factoryRegistry.create(this.xmlConfigFile));
        } catch (Exception e) {
            System.out.println("Unable to initialize HandlerFactoryRegistry from supplied config File");
            System.exit(1);
        }

        filterHandler = new FilterHandler(filterPipeline);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                final ChannelPipeline pipeline = pipeline();
                pipeline.addLast("filterHandler", filterHandler);

//                pipeline.addLast("logger", new SimpleChannelUpstreamHandler() {
//					@Override
//					public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e)
//							throws Exception {
//
//						Object msg = e.getMessage();
//
//						if (msg instanceof ChannelBuffer) {
//
//							if (log.isDebugEnabled()) {
//								ChannelBuffer b = (ChannelBuffer) msg;
//								log.debug("{} => {}",
//										e.getRemoteAddress(),
//										StringUtils.replaceNonPrintableAsciiCharacters(b.toString(CharsetUtil.UTF_8))
//								);
//							}
//
//						} else {
//
//							log.debug("{} => {}", e.getRemoteAddress(), e.getMessage());
//						}
//
//						super.messageReceived(ctx, e);
//					}
//				}
//				);
                return pipeline;
            }
        });


        final ChannelFuture connectFuture = bootstrap.connect(
				new WisebedTestbedAddress(protobufHost, protobufPort, secretReservationKeys)
		);

		connectFuture.awaitUninterruptibly();

		channel = connectFuture.getChannel();
		if (!channel.isConnected()) {
			System.out.println("Could not connect to testbed. This mostly happens due to an invalid or outdated or ended reservation.");
			System.exit(1);
		}
    }

    public void handleCommand(FinnTrProtocol.Envelope command){
        channel.write(command);
    }

}
