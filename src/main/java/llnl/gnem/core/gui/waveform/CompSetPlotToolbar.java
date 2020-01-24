package llnl.gnem.core.gui.waveform;

import javax.swing.JButton;
import llnl.gnem.core.gui.filter.FilterGuiContainer;
import llnl.gnem.core.gui.waveform.plotPrefs.OpenPlotPrefsDialogAction;

/**
 * Created by dodge1 Date: Feb 12, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public class CompSetPlotToolbar extends WaveformViewerToolbar {

    private final TaperPanel taper;
    private final RTrendPanel rtrend;

    public void updateComponents(ThreeComponentModel dataModel) {
        taper.updateState(dataModel);
        rtrend.updateState(dataModel);
    }

    public CompSetPlotToolbar(BasePickingStateManager psMgr,
            FilterGuiContainer container,
            ThreeComponentViewer owner) {

        super(owner);

        taper = new TaperPanel(owner);
        add(taper);
        rtrend = new RTrendPanel(owner);
        add(rtrend);
        
        JButton button = new JButton(new OpenPlotPrefsDialogAction(this));
        addButton(button);

        BasePickingStateManager epsm = psMgr;
        add(epsm);
    }
    
    public void setTaperPanelVisibility( boolean visible )
    {
        taper.setVisible(visible);
    }
    
    public void setRtrendPanelVisibility( boolean visible )
    {
        rtrend.setVisible(visible);
    }

}
