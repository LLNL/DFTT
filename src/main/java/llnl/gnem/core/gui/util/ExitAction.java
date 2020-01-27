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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created by dodge1
 * Date: Feb 12, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */


/**
 * =========================================================================
 * Class to take actions upon exit of code.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class ExitAction extends AbstractAction {

    private static ExitAction ourInstance;

    /**
     * =========================================================================
     * Return an instance of this ExitAction class.
     *
     * @param owner - The Object that owns this instance.
     * @return ourInstance - Instance of this ExitAction class
     */
    public static ExitAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new ExitAction(owner);
        }
        return ourInstance;
    }


    /**
     * =========================================================================
     * Class constructor.
     *
     * @param owner - The Object that owns this instance.
     */
    private ExitAction(Object owner) {
        super("Exit", Utility.getIcon(owner, "miscIcons/exit32.gif"));
        putValue(SHORT_DESCRIPTION, "Click to exit program.");
        putValue(MNEMONIC_KEY, KeyEvent.VK_X);
    }


    /**
     * =========================================================================
     * Do activities based on events.
     *
     * @param e - And ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            System.exit(0);
        } catch (Exception e1) {
            ExceptionDialog.displayError(e1);
        }
    }


}
