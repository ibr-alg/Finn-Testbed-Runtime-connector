package de.tubs.cs.ibr.finn;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.uniluebeck.itm.netty.handlerstack.HandlerFactory;
import de.uniluebeck.itm.tr.util.Tuple;
import org.jboss.netty.channel.ChannelHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 13.09.11
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
public class TileLoadedHandlerFactory implements HandlerFactory {
    public List<Tuple<String, ChannelHandler>> create(String instanceName, Multimap<String, String> properties)
            throws Exception {
        List<Tuple<String, ChannelHandler>> handlers = new LinkedList<Tuple<String, ChannelHandler>>();
		handlers.add(new Tuple<String, ChannelHandler>(instanceName, new TileLoadedHandler(instanceName)));
		return handlers;
    }

    public List<Tuple<String, ChannelHandler>> create(Multimap<String, String> properties)
            throws Exception {
        return create(null,properties);
    }

    public Multimap<String, String> getConfigurationOptions() {
        return HashMultimap.create();  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getDescription() {
        return "A Handler that logs Tile Events";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getName() {
        return "Tile-Events-Handler";  //To change body of implemented methods use File | Settings | File Templates.
    }
}
