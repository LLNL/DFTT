package llnl.gnem.core.gui.waveform.recsec;

import llnl.gnem.core.waveform.components.BaseSingleComponent;


/**
 * User: Doug
 * Date: Sep 26, 2009
 * Time: 2:57:29 PM
 * COPYRIGHT NOTICE
 * Copyright (C) 2008 Doug Dodge.
 */
public class HasOriginWaveformHolder extends BaseWaveformHolder {

    private final double traceAmpToDistFactor;
    public HasOriginWaveformHolder(BaseSingleComponent channelData,
                                  double meanValue,
                                  double thisRange,
                                  double maxRange,
                                  ScalingType scalingType,
                                  double traceAmpToDistFactor,
                                  double referenceTime) {
        super(thisRange,channelData,scalingType,meanValue,maxRange,referenceTime);
        this.traceAmpToDistFactor = traceAmpToDistFactor;
    }

    @Override
    public float[] getPlotArray() {
        return super.getPlotArray(traceAmpToDistFactor);
    }
}