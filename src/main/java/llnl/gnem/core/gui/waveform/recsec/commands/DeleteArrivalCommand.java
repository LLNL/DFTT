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
public class DeleteArrivalCommand implements Command {

    private MultiStationWaveformDataModel dataModel;
    private BaseSingleComponent channelData;
    private DisplayArrival arrival;

    public DeleteArrivalCommand(final BaseSingleComponent channelData,
                               final DisplayArrival arrival) {
        dataModel = WaveformViewerFactoryHolder.getInstance().getMSWaveformDataModel();
        this.channelData = channelData;
        this.arrival = arrival;
    }

    @Override
    public boolean execute() {
        dataModel.removeArrival(channelData, arrival);
        return true;
    }

    @Override
    public boolean unexecute() {
        dataModel.reinstatePick(channelData, arrival);
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