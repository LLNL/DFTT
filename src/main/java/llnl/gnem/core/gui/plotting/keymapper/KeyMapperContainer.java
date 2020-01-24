/**
 * Created by: dodge1
 * Date: Dec 21, 2004
 *
 *  COPYRIGHT NOTICE
 *  GnemUtils Version 1.0
 *  Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
package llnl.gnem.core.gui.plotting.keymapper;


import llnl.gnem.core.gui.util.FramePositionManager;

import javax.swing.*;
import java.awt.*;


public class KeyMapperContainer extends JFrame  {

    public static KeyMapperContainer getInstance()
    {
        if( _instance == null )
            _instance = new KeyMapperContainer();
        return _instance;
    }

    private KeyMapperContainer()
    {
        SetUPFrameManagement( "llnl/gnem/plotting/keymapper", 400, 400 );
        setTitle( "Control Key Mappings"  );

    }


    public void addGuiComponents( KeyMapperGui gui )
    {
        getContentPane().add( gui, BorderLayout.CENTER );
    }


    private static KeyMapperContainer _instance = null;

    private void SetUPFrameManagement( String preferencePath, int width, int height )
    {
        int initialLeft = CONTROL_LEFT;
        int initialTop = CONTROL_TOP;
        int initialWidth = width;
        int initialHeight = height;
        this.addComponentListener( new FramePositionManager( preferencePath, this,
                                                             initialLeft, initialTop, initialWidth, initialHeight ) );
    }


    private static final int CONTROL_LEFT = 100;
    private static final int CONTROL_TOP = 100;
}

