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
