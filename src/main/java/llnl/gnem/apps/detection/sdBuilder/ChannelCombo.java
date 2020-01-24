package llnl.gnem.apps.detection.sdBuilder;

import llnl.gnem.apps.detection.sdBuilder.waveformViewer.CorrelatedTracesModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import llnl.gnem.core.util.StreamKey;

public class ChannelCombo extends JComboBox {

    private static final long serialVersionUID = 782410704625579814L;
    private boolean listenerIsActive = false;

    private ChannelCombo() {
        addActionListener(new MyListener());
    }

    public static ChannelCombo getInstance() {
        return ChannelComboHolder.INSTANCE;
    }

    public void enableActionListener(boolean b) {
        listenerIsActive = b;
    }

    private static class ChannelComboHolder {

        private static final ChannelCombo INSTANCE = new ChannelCombo();
    }

    private class MyListener implements ActionListener {

        public MyListener() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object obj = ChannelCombo.this.getSelectedItem();
            if (obj instanceof StreamKey) {
                StreamKey key = (StreamKey) obj;
                if (listenerIsActive) {
                    CorrelatedTracesModel.getInstance().setSelectedChannel(key);
                }
            }
        }
    }
}
