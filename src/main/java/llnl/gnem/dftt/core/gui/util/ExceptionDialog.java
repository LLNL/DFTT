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
package llnl.gnem.dftt.core.gui.util;


import javax.swing.*;

/**
 * @author ganz
 *         This class has several methods to display error/informational messages to a dialog box.
 *         You can send a database exception object, a string or a string array
 *         You may also optionally set the title.
 */
public class ExceptionDialog extends MessageDialog {
    // ERROR_MESSAGE = 0
    // PLAIN_MESSAGE = -1
    // INFORMATIONAL_MESSAGE = 1

    public ExceptionDialog()
    {
        //  this.setSize(600,200);
    }

    public static void displayError( Exception e )
    {
        ExceptionDialog smd = new ExceptionDialog();
        smd.SetMessageType( JOptionPane.WARNING_MESSAGE );
        smd.displayException( e );
        smd.dispose();
    }
}
