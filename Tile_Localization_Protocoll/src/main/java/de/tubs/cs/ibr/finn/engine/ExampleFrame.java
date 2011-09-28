package de.tubs.cs.ibr.finn.engine;

import eu.funinnumbers.util.eventconsumer.EventConsumer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.MemoryImageSource;

/**
 * Created by IntelliJ IDEA.
 * User: maxpagel
 * Date: 21.09.11
 * Time: 16:28
 * To change this template use File | Settings | File Templates.
 */
public class ExampleFrame extends JFrame{


    public ExampleFrame()
            throws HeadlessException {
        super("Example");
        final int[] pixels = new int[16 * 16];
        final Image image = Toolkit.getDefaultToolkit().createImage(
                new MemoryImageSource(16, 16, pixels, 0, 16));
        final Cursor transparentCursor =
                Toolkit.getDefaultToolkit().createCustomCursor
                        (image, new Point(0, 0), "invisibleCursor");

        // Make mouse cursor transparent (i.e. hide)
        setCursor(transparentCursor);

        //Get Screen Size
        final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // make sure frame is maximized state
        setExtendedState(Frame.MAXIMIZED_BOTH);

        // also remove any decorations
        setUndecorated(true);

        //Set default close operation on the JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set Frames Dimensions
        setSize(dim.width, dim.height);

        //Set frame's visibility
        setVisible(true);

        //Set frames background Color
        getContentPane().setBackground(Color.black);

        // Processing panel for visual output.
        final ExampleGame game = new ExampleGame();
        game.init();
        this.add(game);

        //Initialize EventConsumer. Set MagnetizeWords panel as observer to EventConsumer.
        EventConsumer.getInstance().addObserver(game);
    }

}
