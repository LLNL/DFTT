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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import llnl.gnem.dftt.core.dataAccess.dataObjects.continuous.StreamAvailability;
import llnl.gnem.dftt.core.gui.util.SpringUtilities;
import llnl.gnem.dftt.core.util.StreamKey;

/**
 *
 * @author dodge
 */
public class ChannelDisplayPanel extends JPanel {

    Map<JCheckBox, StreamAvailability> items;

    public ChannelDisplayPanel(Collection<StreamAvailability> candidates) {
        super(new SpringLayout());
        items = new HashMap<>();
        int index = 0;
        for(StreamAvailability sa : candidates){
            JCheckBox jcb = new JCheckBox(sa.toString());
            add(jcb);
            items.put(jcb,sa);
            ++index;
        }
        this.setBorder(BorderFactory.createTitledBorder("Channel Selection"));
        SpringUtilities.makeCompactGrid(this,
                index, 1, //rows, cols
                5, 5, //initX, initY
                5, 5);       //xPad, yPad

    }
    
    public Collection<StreamKey> getSelectedChannels()
    {
        ArrayList<StreamKey> result = new ArrayList<>();
        for(JCheckBox jcb : items.keySet()){
            if(jcb.isSelected()){
                StreamAvailability sa = items.get(jcb);
                result.add(sa.getKey());
            }
        }
        Collections.sort(result);
        return result;
    }

}
