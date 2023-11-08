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
package llnl.gnem.apps.detection.sdBuilder.arrayDisplay.actions;


import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import llnl.gnem.apps.detection.sdBuilder.arrayDisplay.MakeFKMeasurementWorker;

import llnl.gnem.dftt.core.gui.util.Utility;

/**
 * Created by dodge1
 * Date: Mar 22, 2010
 * COPYRIGHT NOTICE
 * Copyright (C) 2007 Lawrence Livermore National Laboratory.
 */
@SuppressWarnings({"NonThreadSafeLazyInitialization"})
public class MakeFKMeasurementAction extends AbstractAction {
    private static MakeFKMeasurementAction ourInstance;

    public static MakeFKMeasurementAction getInstance(Object owner)
    {
        if( ourInstance == null )
            ourInstance = new MakeFKMeasurementAction(owner);
        return ourInstance;
    }

    private MakeFKMeasurementAction(Object owner)
    {
        super( "FK", Utility.getIcon(owner,"miscIcons/fkStack.gif") );
        putValue(SHORT_DESCRIPTION, "Compute FK statistics");
        putValue(MNEMONIC_KEY, KeyEvent.VK_C );
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        new MakeFKMeasurementWorker().execute();
    }

}