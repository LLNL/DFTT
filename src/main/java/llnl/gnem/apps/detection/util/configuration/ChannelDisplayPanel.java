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
package llnl.gnem.apps.detection.util.configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import llnl.gnem.core.gui.util.SpringUtilities;

/**
 *
 * @author dodge
 */
public class ChannelDisplayPanel extends JPanel {

    private final Map<JCheckBox, ChanInfo> chkBoxChanMap;

    public ChannelDisplayPanel(Collection<ChanInfo> channels, String refsta) {
        super(new SpringLayout());
        String target = refsta;
        if( target == null)
            target = "these stations";
        String text = String.format("<html><h2>These are possible channels (FDSN naming convention) that may be used with %s</h2><br>Please choose one or more.</html>", target);
        JLabel label = new JLabel(text);
        add(label);
        chkBoxChanMap = new HashMap<JCheckBox, ChanInfo>();
        for (ChanInfo ci : channels) {
            JCheckBox check = new JCheckBox(ci.toString());
            add(check);
            chkBoxChanMap.put(check, ci);
        }

        this.setBorder(BorderFactory.createTitledBorder("Channel Selection"));
        SpringUtilities.makeCompactGrid(this,
                channels.size() + 1, 1, //rows, cols
                5, 5, //initX, initY
                5, 5);       //xPad, yPad

    }

    Collection<? extends ChanInfo> getSelectedChannels() {
        Collection<ChanInfo> result = new ArrayList<ChanInfo>();
        for (JCheckBox box : chkBoxChanMap.keySet()) {
            if (box.isSelected()) {
                result.add(chkBoxChanMap.get(box));
            }
        }
        return result;
    }
}
