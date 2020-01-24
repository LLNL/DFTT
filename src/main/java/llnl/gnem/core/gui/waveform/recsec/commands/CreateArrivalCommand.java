/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.recsec.commands;

import llnl.gnem.core.util.Command;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.gui.waveform.factory.WaveformViewerFactoryHolder;
import llnl.gnem.core.gui.waveform.DisplayArrival;
import llnl.gnem.core.gui.waveform.recsec.MultiStationWaveformDataModel;

/**
 *
 * @author dodge1
 */
public class CreateArrivalCommand implements Command {

    private MultiStationWaveformDataModel dataModel;
    private BaseSingleComponent channelData;
    private double time;
    private String phase;
    private DisplayArrival arrival;

    public CreateArrivalCommand(final BaseSingleComponent channelData,
                                final double time,
                                final String phase) {
        dataModel = WaveformViewerFactoryHolder.getInstance().getMSWaveformDataModel();
        this.channelData = channelData;
        this.time = time;
        this.phase = phase;
        arrival = null;
    }

    @Override
    public boolean execute() {
        if (arrival == null)
            arrival = dataModel.createPick(channelData, time, phase);
        else
            dataModel.reinstatePick(channelData, arrival);
        return true;
    }

    @Override
    public boolean unexecute() {
        dataModel.removeArrival(channelData, arrival);
        return true;
    }

    @Override
    public boolean isAllowable() {
        return true;
    }

    @Override
    public boolean isReversible() {
        return true;
    }

    @Override
    public boolean isRunInNewThread() {
        return false;
    }
}