package llnl.gnem.core.gui.waveform.filterBank;

import java.util.Collection;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.gui.waveform.WaveformView;

/**
 *
 * @author addair1
 */
public interface BaseFilterBankView extends WaveformView {
    public void clear();

    public void setData(Collection<SeisFilterData> data, BaseSingleComponent aComp);
}
