package de.tubs.cs.ibr.finn;


import de.uniluebeck.itm.netty.handlerstack.isense.ISensePacket;
import de.uniluebeck.itm.tr.util.StringUtils;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.engine.event.EventForwarder;
import eu.funinnumbers.engine.rmi.SGEngineInterface;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 13.09.11
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
public class TileLoadedHandler extends SimpleChannelHandler {
    private final Logger log;

    protected SGEngineInterface engine;

    public TileLoadedHandler(String instanceName ) {
        log = LoggerFactory.getLogger(instanceName != null ? instanceName : TileLoadedHandler.class.getName());
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        log.debug("{}", e);
        super.channelConnected(ctx, e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e)
            throws Exception {
        log.debug("{}", e);
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e)
            throws Exception {
        if (!(e instanceof MessageEvent)) {
            ctx.sendDownstream(e);
            return;
        }
        super.handleDownstream(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        Object msg = e.getMessage();

        if (msg instanceof ChannelBuffer) {

            if (log.isDebugEnabled()) {
                ChannelBuffer b = (ChannelBuffer) msg;
                log.debug(
                        "Received channel buffer: {}",
                        StringUtils.toHexString(b.array(), b.readerIndex(), b.readableBytes()));
            }

        } else if (msg instanceof ISensePacket) {
            ISensePacket iSensePacket = (ISensePacket) msg;
            byte[] packet = iSensePacket.getPayload().array();
            log.debug("" + (char)packet[0] + (char)packet[1] + (char)packet[2]);
            if (packet[1] == 'J') { //Tile loaded Packet
                if (packet.length != 4) {
                    log.error("invalid TileLoaded packet!");

                } else {
//                    if (ctx.getChannel().getRemoteAddress() instanceof WisebedNodeAddress) {
//                        WisebedNodeAddress remoteAddress = (WisebedNodeAddress) ctx.getChannel().getRemoteAddress();
                        int id = 0xFF & packet[2];
                        int isLoaded = 0xFF & packet[1];
                        log.info("Tile " + id + " is loaded " + isLoaded);
//                    }
                            final Event movedPerson = new Event();
                    movedPerson.setType("MWmove");
                    movedPerson.setDescription("Tile with id " + id + " loaded");
                    EventForwarder.getInstance().sendEvent(movedPerson);

                }
            }
        } else {

            log.debug("{}", e.getMessage());
        }

        super.messageReceived(ctx, e);
    }
}
