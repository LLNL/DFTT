package llnl.gnem.core.gui.plotting.keymapper;

import llnl.gnem.core.gui.plotting.MouseMode;

import java.awt.event.KeyEvent;

/**
 * Created by: dodge1
 * Date: Dec 21, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class DefaultControlKeyMapper implements ControlKeyMapper {
    public MouseMode getMouseMode( int keyCode )
    {
        if( keyCode == KeyEvent.VK_SHIFT ){
            return ( MouseMode.PAN );
        }
        else if( keyCode == KeyEvent.VK_ESCAPE ){
            return ( MouseMode.ZOOM_ONLY );
        }
        else if( keyCode == KeyEvent.VK_CONTROL ){
            return ( MouseMode.CONTROL_SELECT );
        }
        else if( keyCode == KeyEvent.VK_ALT ){
            return ( MouseMode.SELECT_REGION );
        }
        else
            return null;

    }

    public boolean isDeleteKey( int keyCode )
    {
        return keyCode == KeyEvent.VK_DELETE;
    }

    public boolean isControlKey( int keyCode )
    {
        return keyCode == KeyEvent.VK_CONTROL;
    }
}
