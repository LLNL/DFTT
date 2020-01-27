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
import javax.swing.text.MaskFormatter;

/**
 * User: dodge1
 * Date: May 13, 2004
 * Time: 1:50:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class JTimeField extends JFormattedTextField {

    public JTimeField()
    {
        super( createFormatter() );
    }

    protected static MaskFormatter createFormatter()
    {
        MaskFormatter formatter = null;
        try {
            formatter = new MaskFormatter( "####/##/##_##:##:##.###" );
            formatter.setPlaceholderCharacter( '0' );
            formatter.setPlaceholder( "1970/01/01_00:00:00.000" );
            formatter.setAllowsInvalid( false );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        return formatter;
    }


}
