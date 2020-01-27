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

