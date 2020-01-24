package llnl.gnem.core.gui.plotting.keymapper;

import llnl.gnem.core.gui.plotting.MouseMode;

/**
 * Created by: dodge1
 * Date: Dec 21, 2004
 * COPYRIGHT NOTICE
 * GnemUtils Version 1.0
 * Copyright (C) 2004 Lawrence Livermore National Laboratory.
 */
public class KeyMap {

    public KeyMap( int keyCode, String description, MouseMode mouseMode )
    {
        this.keyCode = keyCode;
        this.description = description;
        this.mouseMode = mouseMode;
    }

    public void setKeyCode( int code )
    {
        keyCode = code;
    }
    public int getKeyCode()
    {
        return keyCode;
    }

    public String getDescription()
    {
        return description;
    }

    public MouseMode getMouseMode()
    {
        return mouseMode;
    }

    private int keyCode;
    private String description;
    private MouseMode mouseMode;
}
