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

public class MoveArrivalCommand implements Command {

    private MultiStationWaveformDataModel dataModel;
    private BaseSingleComponent channelData;
    private double deltaT;
    private DisplayArrival arrival;

    public MoveArrivalCommand(final BaseSingleComponent channelData,
                               final double deltaT,
                               final DisplayArrival arrival) {
        dataModel = WaveformViewerFactoryHolder.getInstance().getMSWaveformDataModel();
        this.channelData = channelData;
        this.deltaT = deltaT;
        this.arrival = arrival;
    }

    @Override
    public boolean execute() {
        arrival.incrementModificationCount();
        dataModel.movePick(channelData, arrival, deltaT);
        return true;
    }

    @Override
    public boolean unexecute() {
        arrival.decrementModificationCount();
        dataModel.movePick(channelData, arrival, -deltaT);
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