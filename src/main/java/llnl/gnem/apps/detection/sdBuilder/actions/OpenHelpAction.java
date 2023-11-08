/*-
 * #%L
 * Detection Framework (Release)
 * %%
 * Copyright (C) 2015 - 2023 Lawrence Livermore National Laboratory (LLNL)
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
package llnl.gnem.apps.detection.sdBuilder.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;

import llnl.gnem.apps.detection.sdBuilder.help.BuilderHelpFrame;
import llnl.gnem.dftt.core.gui.util.Utility;

public class OpenHelpAction extends AbstractAction {
    private static OpenHelpAction ourInstance;

    public static OpenHelpAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new OpenHelpAction(owner);
        }
        return ourInstance;
    }

    private OpenHelpAction(Object owner) {
        super("Key descriptions", Utility.getIcon(owner, "miscIcons/QuestionMark.png"));
        putValue(SHORT_DESCRIPTION, "Show the Builder help dialog");
        putValue(MNEMONIC_KEY, KeyEvent.VK_K);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BuilderHelpFrame.getInstance().setVisible(true);
    }
}
