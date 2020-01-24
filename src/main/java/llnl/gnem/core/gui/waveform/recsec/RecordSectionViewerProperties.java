/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package llnl.gnem.core.gui.waveform.recsec;

/**
 *
 * @author dodge1
 */
public class RecordSectionViewerProperties {
    private ScalingType scalingType;
    private TimeReductionType timeReductionType;
    private RecordSectionViewerProperties() {
        scalingType = ScalingType.Fixed;
        timeReductionType = TimeReductionType.None;
    }

    public static RecordSectionViewerProperties getInstance() {
        return RecordSectionViewerPropertiesHolder.INSTANCE;
    }

    ScalingType getScalingType() {
        return ScalingType.Fixed;//scalingType;
    }

    TimeReductionType getTimeReductionType() {
        return timeReductionType;
    }

    boolean isUseZoomBox() {
        return true;
    }

    boolean isAxisTicksVisible() {
        return false;
    }

    public void setScalingType(ScalingType scalingType) {
        this.scalingType = scalingType;
    }

    public void setTimeReductionType(TimeReductionType timeReductionType) {
        this.timeReductionType = timeReductionType;
    }

    private static class RecordSectionViewerPropertiesHolder {

        private static final RecordSectionViewerProperties INSTANCE = new RecordSectionViewerProperties();
    }
}
