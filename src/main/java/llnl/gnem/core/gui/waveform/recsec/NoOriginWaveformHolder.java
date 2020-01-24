package llnl.gnem.core.gui.waveform.recsec;

import llnl.gnem.core.waveform.components.BaseSingleComponent;


/**
 * User: Doug
 * Date: Sep 26, 2009
 * Time: 2:57:29 PM
 * COPYRIGHT NOTICE
 * Copyright (C) 2008 Doug Dodge.
 */
public class NoOriginWaveformHolder extends BaseWaveformHolder {

    public NoOriginWaveformHolder(BaseSingleComponent channelData,
                                  double meanValue,
                                  double thisRange,
                                  double maxRange,
                                  ScalingType scalingType,
                                  double referenceTime) {
        super(thisRange, channelData, scalingType, meanValue, maxRange,referenceTime);
    }

    @Override
    public float[] getPlotArray() {
        return super.getPlotArray(1.0);
    }

}
