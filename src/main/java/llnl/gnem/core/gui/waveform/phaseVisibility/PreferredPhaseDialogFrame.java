package llnl.gnem.core.gui.waveform.phaseVisibility;

import java.awt.BorderLayout;
import llnl.gnem.core.gui.util.PersistentPositionContainer;

public class PreferredPhaseDialogFrame extends PersistentPositionContainer {
    public static final int FRAME_WIDTH = 300, FRAME_HEIGHT = 450;
    private static final long serialVersionUID = 8410975666346296781L;

    private PreferredPhaseDialogFrame() {
        super("llnl/gnem/apps/waveform/plot/phaseVisibility/preferredDialog", "Preferred Phase Selection", FRAME_WIDTH, FRAME_HEIGHT);
        getContentPane().add(new AllowablePhaseGUI(), BorderLayout.CENTER);

        updateCaption();
    }

    public static PreferredPhaseDialogFrame getInstance() {
        return FrameHolder.INSTANCE;
    }

    private static class FrameHolder {
        private static final PreferredPhaseDialogFrame INSTANCE = new PreferredPhaseDialogFrame();
    }

    @Override
    protected void updateCaption() {
        setTitle(shortTitle);
    }
}
