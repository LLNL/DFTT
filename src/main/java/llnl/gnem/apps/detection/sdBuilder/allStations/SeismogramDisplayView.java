/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.apps.detection.sdBuilder.allStations;


/**
 *
 * @author dodge1
 */
public class SeismogramDisplayView implements SeismogramView {

    private final MultiSeismogramPlot seismogramPlot;

    public SeismogramDisplayView() {
        seismogramPlot = new MultiSeismogramPlot();
        AllStationsPickModel.getInstance().setSeismogramPlot(seismogramPlot);
    }

    public MultiSeismogramPlot getSeismogramPlot() {
        return seismogramPlot;
    }


    @Override
    public void dataHaveChanged() {
        seismogramPlot.updateForChangedData();
    }
}
