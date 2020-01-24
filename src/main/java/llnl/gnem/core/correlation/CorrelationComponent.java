package llnl.gnem.core.correlation;

import llnl.gnem.core.seismicData.EventInfo;
import llnl.gnem.core.correlation.util.NominalArrival;
import llnl.gnem.core.dataAccess.dataObjects.StreamEpochInfo;
import llnl.gnem.core.gui.map.stations.StationInfo;
import llnl.gnem.core.metadata.site.core.CssSite;
import llnl.gnem.core.util.Geometry.EModel;
import llnl.gnem.core.util.Epoch;
import llnl.gnem.core.waveform.components.BaseSingleComponent;
import llnl.gnem.core.waveform.components.ComponentIdentifier;
import llnl.gnem.core.waveform.components.RotationStatus;
import llnl.gnem.core.waveform.responseProcessing.TransferStatus;

/**
 *
 * @author dodge1
 */
public class CorrelationComponent extends BaseSingleComponent implements Comparable {

    private final CorrelationEventInfo event;
    private double shift;
    private double correlation;
    private double std;
    private final String refstaName;
    private final Double dnorth;
    private final Double deast;
    private final boolean refsta;
    private final boolean element;

    public CorrelationComponent(CorrelationComponent other) {
        super(other);
        event = new CorrelationEventInfo(other.event);
        shift = other.shift;
        correlation = other.correlation;
        std = other.std;
        refstaName = other.refstaName;
        dnorth = other.dnorth;
        deast = other.deast;
        refsta = other.refsta;
        element = other.element;
    }

    /**
     * @return the refsta
     */
    public String getRefstaName() {
        return refstaName;
    }

    /**
     * @return the dnorth
     */
    public Double getDnorth() {
        return dnorth;
    }

    /**
     * @return the deast
     */
    public Double getDeast() {
        return deast;
    }

    /**
     * @return the isRefsta
     */
    public boolean isRefsta() {
        return refsta;
    }

    /**
     * @return the isElement
     */
    public boolean isElement() {
        return element;
    }

    public CorrelationComponent(StationInfo stationInfo,
            ComponentIdentifier identifier,
            CorrelationTraceData ctd,
            TransferStatus transferStatus,
            RotationStatus rotationStatus,
            EventInfo event, StreamEpochInfo sei) {
        super(stationInfo, identifier, transferStatus, rotationStatus, ctd, sei);
        this.event = new CorrelationEventInfo(event);
        shift = 0;
        correlation = 0;
        if (stationInfo != null && stationInfo instanceof CssSite) {
            CssSite site = (CssSite) stationInfo;
            refstaName = site.getRefsta();
            dnorth = site.getDnorth();
            deast = site.getDeast();
            refsta = site.isArrayRefsta();
            element = site.isNonRefstaArrayElement();
        } else {
            refstaName = null;
            dnorth = null;
            deast = null;
            refsta = false;
            element = false;

        }
    }

    public CorrelationComponent(BaseSingleComponent source, CorrelationEventInfo event) {
        super(source);
        this.event = event;
        shift = 0;
        correlation = 0;
        refstaName = null;
        dnorth = null;
        deast = null;
        refsta = false;
        element = false;
    }

    public boolean isArrayComponent() {
        return refsta || element;
    }

    public EventInfo getEvent() {
        return event;
    }

    public double getDegDist() {
        if (event != null && this.getStationInfo() != null) {
            return EModel.getDeltaWGS84(getEvent().getLat(), getEvent().getLon(),
                    getStationInfo().getLat(), getStationInfo().getLon());
        } else {
            return -999.0;
        }
    }

    public void setCorrelation(double correlation) {
        this.correlation = correlation;
    }

    public void setShift(double shift) {
        this.shift = shift;
    }

    public double getShift() {
        return shift;
    }

    public double getCorrelation() {
        return correlation;
    }

    public void setStd(double std) {
        this.std = std;
    }

    public double getStd() {
        return std;
    }

    public NominalArrival getNominalPick() {
        CorrelationTraceData ctd = (CorrelationTraceData) getTraceData();
        return ctd.getNominalPick();
    }

    public String getChan() {
        return getTraceData().getChan();
    }

    public boolean containsWindow() {
        CorrelationProcessingParamsInitial cls = new CorrelationProcessingParamsInitial();

        double prePhaseOffset = 5; // 
        double postPhaseOffset = 10;

        CorrelationTraceData ctd = (CorrelationTraceData) getTraceData();
        NominalArrival arrival = ctd.getNominalPick();
        double start = arrival.getTime() - prePhaseOffset;
        double end = arrival.getTime() + postPhaseOffset;
        Epoch epoch = new Epoch(start, end);
        return ctd.getSeismogram().contains(epoch, true);
    }

    @Override
    public int compareTo(Object t) {
        CorrelationComponent other = (CorrelationComponent) t;
        if (this.event.getEvid() > other.event.getEvid()) {
            return 1;
        } else if (this.event.getEvid() < other.event.getEvid()) {
            return -1;
        } else {
            return 0;
        }
    }
}
