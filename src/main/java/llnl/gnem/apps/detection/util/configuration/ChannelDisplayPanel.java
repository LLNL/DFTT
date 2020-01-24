/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
