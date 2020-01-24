package llnl.gnem.core.gui.waveform;

import java.util.Collection;
import llnl.gnem.core.util.Command;
import llnl.gnem.core.util.ButtonAction;
import llnl.gnem.core.waveform.components.ComponentSet;

/**
 * Created by dodge1 Date: Mar 19, 2010 COPYRIGHT NOTICE Copyright (C) 2007
 * Lawrence Livermore National Laboratory.
 */
public interface Base3CWaveformDataView extends WaveformView {
    public void pushCommand(Command command);

    public void saveStarted();

    public void saveCompleted();

    public void setUsable(boolean usable);

    void clearComponentDisplays();

    void addComponentSet(ComponentSet set);

    public void componentSetsCompleted();

    void updateForChangedComponentSets();

    void updateForComponentRotation(Collection<ComponentSet> rotatable);

    void updateForUndoneTransferOperation();

    void updateForTransferredComponentSet(ComponentSet set);

    void updateForFilterOperation();

    void updateForChangedData();

    public ButtonAction getButtonAction(Class<? extends ButtonAction> className);
}
