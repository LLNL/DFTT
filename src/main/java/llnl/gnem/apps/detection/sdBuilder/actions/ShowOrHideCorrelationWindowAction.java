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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import llnl.gnem.apps.detection.sdBuilder.configuration.ParameterModel;
import llnl.gnem.apps.detection.sdBuilder.waveformViewer.ClusterBuilderFrame;
import llnl.gnem.dftt.core.gui.util.Utility;

public class ShowOrHideCorrelationWindowAction extends AbstractAction {

    private static ShowOrHideCorrelationWindowAction ourInstance;

    private boolean visible = true;

    public static ShowOrHideCorrelationWindowAction getInstance(Object owner) {
        if (ourInstance == null) {
            ourInstance = new ShowOrHideCorrelationWindowAction(owner);
        }
        return ourInstance;
    }

    private final ImageIcon visibleIcon;
    private final ImageIcon hiddenIcon;

    private ShowOrHideCorrelationWindowAction(Object owner) {
        super("Show", new ImageIcon(Utility.getIcon(owner, "miscIcons/showerror16.gif").getImage().getScaledInstance(32,
                32, Image.SCALE_DEFAULT)));
        putValue(SHORT_DESCRIPTION, "Hide correlation window");
        putValue(MNEMONIC_KEY, KeyEvent.VK_M);
        visibleIcon = new ImageIcon(Utility.getIcon(owner, "miscIcons/showerror16.gif").getImage().getScaledInstance(32,
                32, Image.SCALE_DEFAULT));
        hiddenIcon = new ImageIcon(Utility.getIcon(owner, "miscIcons/viewStack.gif").getImage().getScaledInstance(32,
                32, Image.SCALE_DEFAULT));
        visible = ParameterModel.getInstance().isShowCorrelationWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        visible = !visible;
        ParameterModel.getInstance().setShowCorrelationWindow(visible);
        ClusterBuilderFrame.getInstance().setCorrelationWindowVisible(visible);
        if (visible) {
            putValue(SHORT_DESCRIPTION, "Hide correlation window");
            putValue(LARGE_ICON_KEY, visibleIcon);
        } else {
            putValue(SHORT_DESCRIPTION, "Show correlation window");
            putValue(LARGE_ICON_KEY, hiddenIcon);
        }
    }

    public boolean isVisible() {
        return visible;
    }

}
