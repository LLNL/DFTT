package llnl.gnem.core.gui.plotting.keymapper;

import llnl.gnem.core.gui.plotting.MouseMode;


/**
 * Created by: dodge1
 * Date: Dec 21, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public interface ControlKeyMapper {
    MouseMode getMouseMode( int keyCode );

    boolean isDeleteKey( int keyCode );

    boolean isControlKey( int keyCode );
}
