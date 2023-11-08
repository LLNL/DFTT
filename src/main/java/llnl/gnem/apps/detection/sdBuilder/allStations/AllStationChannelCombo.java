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
package llnl.gnem.apps.detection.sdBuilder.allStations;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import llnl.gnem.dftt.core.util.StreamKey;

public class AllStationChannelCombo extends JComboBox {

    private static final long serialVersionUID = 782410704625579814L;
    private boolean listenerIsActive = false;

    private AllStationChannelCombo() {
        addActionListener(new MyListener());
    }

    public static AllStationChannelCombo getInstance() {
        return ChannelComboHolder.INSTANCE;
    }

    public void enableActionListener(boolean b) {
        listenerIsActive = b;
    }

    private static class ChannelComboHolder {

        private static final AllStationChannelCombo INSTANCE = new AllStationChannelCombo();
    }

    private class MyListener implements ActionListener {

        public MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object obj = AllStationChannelCombo.this.getSelectedItem();
            if (obj instanceof String) {
                String key = (String) obj;
                if (listenerIsActive) {
                    SeismogramModel.getInstance().channelWasChanged();
                }
            }
        }
    }
}
