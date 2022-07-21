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
    public MouseMode getMouseMode( KeyEvent keyEvent )
    {
        int keyCode = keyEvent.getKeyCode();
        if( keyCode == KeyEvent.VK_SHIFT ){
            return ( MouseMode.PAN2 );
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
