package de.tubs.cs.ibr.finn.handlers;


import com.google.protobuf.InvalidProtocolBufferException;
import de.tubs.cs.ibr.finn.ExampleProtocol;
import de.tubs.cs.ibr.finn.station.FinnTrProtocol;
import de.uniluebeck.itm.netty.handlerstack.isense.ISensePacket;
import de.uniluebeck.itm.nettywisebed.WisebedNodeAddress;
import de.uniluebeck.itm.tr.util.StringUtils;
import eu.funinnumbers.db.model.event.Event;
import eu.funinnumbers.engine.event.EventForwarder;
import eu.funinnumbers.engine.rmi.SGEngineInterface;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.jboss.netty.buffer.ChannelBuffers.wrappedBuffer;
import static org.jboss.netty.channel.Channels.write;

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

    public TileLoadedHandler(String instanceName) {
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
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent evt)
            throws Exception {
        MessageEvent e = (MessageEvent) evt;
        Object originalMessage = e.getMessage();
        if (!(evt instanceof MessageEvent)) {
            ctx.sendDownstream(evt);
            return;
        }
        if (!(originalMessage instanceof FinnTrProtocol.Envelope)) {
            ctx.sendDownstream(evt);
        }


        FinnTrProtocol.Envelope message = (FinnTrProtocol.Envelope) originalMessage;
        ExampleProtocol.LedColorStatus led_cmd = ExampleProtocol.LedColorStatus.parseFrom(message.getCommand());
        Object encodedMessage = encode(ctx, e.getChannel(), originalMessage);
        if (originalMessage == encodedMessage) {
            ctx.sendDownstream(evt);
        } else if (encodedMessage != null) {
            write(ctx, e.getFuture(), encodedMessage, new WisebedNodeAddress(led_cmd.getNodeAddress()));
        }

    }

    private Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) {
        FinnTrProtocol.Envelope message = (FinnTrProtocol.Envelope) msg;
        if (message.getType().equals("LED")) {
            try {
                ExampleProtocol.LedColorStatus led_cmd = ExampleProtocol.LedColorStatus.parseFrom(message.getCommand());
                if ((led_cmd.getRed() > 0xFF) || (led_cmd.getGreen() > 0xFF) || (led_cmd.getBlue() > 0xFF)) {
                    return null;
                } else if ((led_cmd.getRed() < 0) || (led_cmd.getGreen() < 0) || (led_cmd.getBlue() < 0)) {
                    return null;
                } else {
                    byte[] packet = new byte[5];
                    packet[0] = 10;
                    packet[1] = 'L';
                    packet[2] = (byte) led_cmd.getBlue();
                    packet[3] = (byte) led_cmd.getGreen();
                    packet[4] = (byte) led_cmd.getRed();

//                    WisebedProtocol.Message wisebed_message = WisebedProtocol.Message.newBuilder().setNodeBinary(
//                            WisebedProtocol.Message.NodeBinary.newBuilder().setData(ByteString.copyFrom(packet))
//                    .addDestinationNodeUrns("urn:wisebed:tubs:408").setSourceNodeUrn("")).setTimestamp("").setType(
//                            WisebedProtocol.Message.Type.NODE_BINARY).build();
//                    return wisebed_message;
                    log.info("Encoded isense Packet " + packet.toString());
                    return new ISensePacket(wrappedBuffer(packet));
                }

            } catch (InvalidProtocolBufferException e) {
                return msg;
            }



        } else return msg;
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
//            log.debug("" + (char) packet[0] + (char) packet[1] + (char) packet[2]);
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
