package llnl.gnem.core.gui.waveform.recsec;

import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.gui.waveform.WaveformView;

/**
 *
 * @author dodge1
 */
public interface BaseMultiChannelWaveformView extends WaveformView {

    void updateForNewEvent();

    void updateForChangedChannel();

    void updateForChangedWaveform(BaseSingleComponent channelData);

    void updateForChangedWaveform();

    void updateForChangedOrigin();

    void updateForAddedOrigin();

    void updateForChangedPreferredOrigin();

    void clear();
}
