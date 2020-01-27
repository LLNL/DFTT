/*
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2020 Lawrence Livermore National Laboratory (LLNL)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package llnl.gnem.core.gui.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.net.URL;

/**
 * Class containing miscellaneous utilities for gui construction.
 */
public class Utility {

    /**
     * Adds a button with an icon to a toolbar.
     *
     * @param owner      The component hosting the toolbar
     * @param toolbar    The toolbar on which the button will be placed
     * @param imageLoc   The name of the GIF file containing the image. This is expected to be
     *                   in the class path. Typically, the GIF file would be in a jar file and the jar file would
     *                   be in the class path.
     * @param tooltip    String containing a tooltip for the button.
     * @param buttonText The text to display on the button
     * @param listener   The action listener for this button
     * @param enabled    The enabled status of the button
     * @return The constructed JButton object
     */
    public static JButton addButton( Object owner, JToolBar toolbar, String imageLoc, String tooltip, String buttonText, java.awt.event.ActionListener listener, boolean enabled )
    {
        ClassLoader cl = owner.getClass().getClassLoader();
        URL imageURL = cl.getResource( imageLoc );
        JButton result;
        if( imageURL != null )
            result = new JButton( new ImageIcon( imageURL ) );
        else
            result = new JButton( "ImageNotFound" );

        result.setMaximumSize( new Dimension( 25, 25 ) );
        result.setToolTipText( tooltip );
        result.setText( buttonText );
        result.addActionListener( listener );
        result.setEnabled( enabled );
        toolbar.add( result );
        return result;

    }

    /**
     * Adds a JToggleButton to a toolbar
     *
     * @param owner    The component hosting the toolbar
     * @param listener The action listener for this button
     * @param toolbar  The toolbar on which the button will be placed
     * @param imageLoc The name of the GIF file containing the image. This is expected to be
     *                 in the class path. Typically, the GIF file would be in a jar file and the jar file would
     *                 be in the class path.
     * @param tooltip  String containing a tooltip for the button.
     * @param enabled  The enabled status of the button
     * @param checked  The checked status of the JToggleButton
     * @return The constructed JToggleButton object
     */
    public static JToggleButton addToggleButton( Object owner, ItemListener listener, JToolBar toolbar, String imageLoc, String tooltip, boolean enabled, boolean checked )
    {
        ClassLoader cl = owner.getClass().getClassLoader();
        URL imageURL = cl.getResource( imageLoc );
        JToggleButton result;
        if( imageURL != null )
            result = new JToggleButton( new ImageIcon( imageURL ) );
        else
            result = new JToggleButton( "ImageNotFound" );

        result.setMaximumSize( new Dimension( 25, 25 ) );
        result.setToolTipText( tooltip );
        result.addItemListener( listener );
        result.setEnabled( enabled );
        result.setSelected( checked );
        toolbar.add( result );
        return result;

    }

    public static ImageIcon getIcon( Object owner, String imageLoc )
    {
        ClassLoader cl = owner.getClass().getClassLoader();
        URL imageURL = cl.getResource( imageLoc );
        if( imageURL != null )
            return new ImageIcon( imageURL );
        else
            return null;
    }

    public static void centerFrame( JFrame frame )
    {
        Dimension dim = frame.getToolkit().getScreenSize();
        Rectangle abounds = frame.getBounds();
        frame.setLocation( ( dim.width - abounds.width ) / 2,
                           ( dim.height - abounds.height ) / 2 );

    }

    public static void forceMouseOverFrame( JFrame frame )
    {
        Dimension dim = frame.getToolkit().getScreenSize();
        Rectangle abounds = frame.getBounds();

        frame.setAlwaysOnTop( true );
        PointerInfo pi = MouseInfo.getPointerInfo();
        Point point = pi.getLocation();
        // Shift the window to the right and up by 10 pixels each way so that a slight
        // jiggle of the mouse does not cause the window to lose focus and close.
        point.x -= 10;
        if( point.x + abounds.width > dim.width )
            point.x = dim.width - abounds.width;
        point.y -= 10;
        if( point.y + abounds.height > dim.height )
            point.y = dim.height - abounds.height;
        frame.setLocation( point );
    }
}
